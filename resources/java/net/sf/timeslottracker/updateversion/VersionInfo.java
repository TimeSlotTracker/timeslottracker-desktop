package net.sf.timeslottracker.updateversion;

/**
 * Version info about some release of TST
 * 
 * @version File version: $Revision: 1165 $, $Date: 2009-08-23 18:01:57 +0700
 *          (Sun, 23 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class VersionInfo {

  private boolean newVersionAvailable;

  private String version;

  private String releaseDate;

  private String notesLink;

  private String filesLink;

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(String releaseDate) {
    this.releaseDate = releaseDate;
  }

  public String getNotesLink() {
    return notesLink;
  }

  public void setNotesLink(String notesLink) {
    this.notesLink = notesLink;
  }

  public String getFilesLink() {
    return filesLink;
  }

  public void setFilesLink(String filesLink) {
    this.filesLink = filesLink;
  }

  public boolean isNewVersionAvailable() {
    return newVersionAvailable;
  }

  public void setNewVersionAvailable(boolean newVersionAvailable) {
    this.newVersionAvailable = newVersionAvailable;
  }

  @Override
  public String toString() {
    return "VersionInfo{" +
        "newVersionAvailable=" + newVersionAvailable +
        ", version='" + version + '\'' +
        ", releaseDate='" + releaseDate + '\'' +
        ", notesLink='" + notesLink + '\'' +
        ", filesLink='" + filesLink + '\'' +
        '}';
  }
}
