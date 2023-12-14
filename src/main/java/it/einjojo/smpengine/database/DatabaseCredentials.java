package it.einjojo.smpengine.database;

public interface DatabaseCredentials {
    String getHost();
    int getPort();
    String getDatabase();
    String getUsername();
    String getPassword();
    int getConnectionTimeout();
}
