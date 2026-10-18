# PlayerAdminPlugin

Paper-Plugin mit `/player`-Adminbefehlen. Die Berechtigung `playeradmin.admin` ist in `plugin.yml` auf `default: op` gesetzt, daher dürfen standardmäßig nur OP-Spieler die Befehle verwenden.

## Befehle
- `/player help`
- `/player info <Spieler>`
- `/player name <Spieler> <AnzeigeName>`
- `/player freeze <Spieler>`
- `/player unfreeze <Spieler>`
- `/player god <Spieler> <on|off>`
- `/player skin <Spieler> <SkinName>` (benötigt SkinsRestorer und dessen passenden Befehl)

## Build lokal
Benötigt JDK 21 und Maven: `mvn clean package`. Die JAR liegt danach in `target/`.

## Build über GitHub
1. ZIP entpacken und den Inhalt in ein neues GitHub-Repository hochladen (die `pom.xml` muss im Hauptverzeichnis liegen).
2. Auf GitHub den Reiter **Actions** öffnen.
3. Den Workflow **Build PlayerAdminPlugin** auswählen und **Run workflow** klicken. Alternativ baut er bei einem Push auf `main`/`master` automatisch.
4. Nach erfolgreichem Lauf den Workflow öffnen und unter **Artifacts** das Artifact `PlayerAdminPlugin` herunterladen. Darin liegt die gebaute JAR.
5. JAR in den Serverordner `plugins` kopieren und den Server neu starten.

Hinweis: Skin-Befehle können je nach SkinsRestorer-Version unterschiedlich sein. Das Plugin wurde nicht auf einem laufenden Paper-Server getestet.
