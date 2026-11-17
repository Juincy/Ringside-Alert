"""Builds schedule.json from TVmaze (https://www.tvmaze.com/api, CC BY-SA).

For each show in tools/shows.json it looks up the show, takes upcoming episode air times
for the next 14 days, and falls back to the show's weekly schedule if no episodes are listed.
Shows TVmaze does not know are skipped and reported in the log.
"""
import json
import sys
import urllib.parse
import urllib.request
from datetime import datetime, time, timedelta, timezone
from zoneinfo import ZoneInfo

API = "https://api.tvmaze.com"
DAYS = 14


def get(path):
    req = urllib.request.Request(API + path, headers={"User-Agent": "RingsideAlert-schedule/1.0"})
    try:
        with urllib.request.urlopen(req, timeout=30) as r:
            return json.load(r)
    except Exception as e:  # network error, 404, rate limit
        print("request failed:", path, e)
        return None


def upcoming(show, now, end):
    starts = []
    for e in get("/shows/%d/episodes" % show["id"]) or []:
        stamp = e.get("airstamp")
        if not stamp:
            continue
        t = datetime.fromisoformat(stamp.replace("Z", "+00:00"))
        if now - timedelta(hours=3) <= t <= end:
            starts.append(t)
    if starts:
        return starts
    sched = show.get("schedule") or {}
    if not sched.get("time") or not sched.get("days"):
        return []
    net = show.get("webChannel") or show.get("network") or {}
    tzname = (net.get("country") or {}).get("timezone") or "America/New_York"
    tz = ZoneInfo(tzname)
    hh, mm = (int(x) for x in sched["time"].split(":")[:2])
    out = []
    for i in range(DAYS + 1):
        d = (now.astimezone(tz) + timedelta(days=i)).date()
        if d.strftime("%A") in sched["days"]:
            t = datetime.combine(d, time(hh, mm), tzinfo=tz)
            if now - timedelta(hours=3) <= t <= end:
                out.append(t)
    return out


def main():
    cfg = json.load(open("tools/shows.json"))
    now = datetime.now(timezone.utc)
    end = now + timedelta(days=DAYS)
    rows = []
    for c in cfg:
        show = get("/singlesearch/shows?q=" + urllib.parse.quote(c["query"]))
        if not show or c["must"].lower() not in show["name"].lower():
            print("skipped %s: TVmaze match was %r" % (c["id"], show and show["name"]))
            continue
        net = show.get("webChannel") or show.get("network") or {}
        watch = net.get("name") or c["watch"]
        starts = upcoming(show, now, end)
        print("%s -> %s (%s): %d airings" % (c["id"], show["name"], watch, len(starts)))
        for t in sorted(set(starts)):
            rows.append({
                "id": c["id"], "company": c["company"], "brand": c["brand"], "name": c["name"],
                "image": c["image"], "network": watch, "startsAt": t.isoformat(),
            })
    if not rows:
        print("no airings found; leaving schedule.json unchanged")
        return 0
    try:
        old = json.load(open("schedule.json")).get("shows")
    except Exception:
        old = None
    if old == rows:
        print("schedule unchanged")
        return 0
    with open("schedule.json", "w") as f:
        json.dump({"updated": now.isoformat(timespec="seconds"), "shows": rows}, f, indent=1)
    print("wrote %d airings" % len(rows))
    return 0


if __name__ == "__main__":
    sys.exit(main())
