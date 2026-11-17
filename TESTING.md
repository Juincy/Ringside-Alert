# Testing checklist

Install the APK (from the Build check artifact, or a release), then go through this list. Note your phone model and Android version for any bug you report.

**First run**
- [ ] App opens and asks for notification permission (Android 13+)
- [ ] Shows tab fills in after a few seconds (needs internet and a filled `schedule.json`)
- [ ] Settings > Schedule shows a count of airings and a "Last updated" time

**Browsing**
- [ ] Company dropdown filters the cards; WWE and AEW show brand chips
- [ ] Tapping a day in the week strip changes the list; dots appear only on days with shows
- [ ] Tapping a card opens details with image, time, and where to watch
- [ ] Star on details adds the show to Favorites; Favorites survive closing the app
- [ ] Light, Dark, and System theme all look right

**Alerts**
- [ ] Settings > Testing alerts > "Send test alert" shows a notification right away
- [ ] "Test in 1 minute": close the app, lock the phone, and the notification arrives
- [ ] Bell on a show turns white when on; the show appears under Alerts > Your alerts
- [ ] Turning a company off under Alerts stops its alerts
- [ ] A real alert arrives for a show you set (try one that starts within a few hours)

**Offline**
- [ ] Turn on airplane mode, reopen the app: the last schedule still shows

**Updates** (after you publish a second release, e.g. v1.0.1)
- [ ] The Shows tab shows an update banner and Settings > Updates lists the new version
- [ ] "Download and install" asks to allow installs from this app, then installs
- [ ] Version number in Settings changes after the update and favorites and alerts are still there

**Known limits**
- Alerts can arrive a minute or two late on phones with strict battery saving.
- Show times come from TVmaze; promotions it does not list will be missing.
