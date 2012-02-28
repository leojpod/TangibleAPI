/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package managers;

/**
 *
 * @author leo
 */
public interface ApplicationManager {
  
  static class Application {

      public String name, description;

      public Application(String name, String description) {
        this.name = name;
        this.description = description;
      }
    }
  
  public String registerApp(String name, String description);

  public String removeApplication(String uuid);
  
  public boolean isAppRegistred(String uuid);
  
  public boolean isAppRunning(String uuid);
  
}
