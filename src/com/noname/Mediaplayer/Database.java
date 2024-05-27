package com.noname.Mediaplayer;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class Database {
    public static ArrayList names = new ArrayList(), paths = new ArrayList(), authors = new ArrayList(), playlists = new ArrayList();
    public static void main(String[] args) throws ClassNotFoundException, SQLException{
        Class.forName("org.sqlite.JDBC");
        read_db();
    }
    public static void read_db() throws ClassNotFoundException, SQLException{
        Connection connection = DriverManager.getConnection("jdbc:sqlite:db.db");
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery("Select * from songs");
        while (res.next()) {
            names.add(res.getString(1));
            paths.add(res.getString(2));
            authors.add(res.getString(3));
        }
        res = statement.executeQuery("SELECT * FROM playlists");
        while (res.next()){
            playlists.add(res.getString(1));
        }
        playlists.remove("songs");
        playlists.remove("playlists");
        statement.close();
        connection.close();
    }

    public static ArrayList read_playlist(String playlist) throws SQLException{
        ArrayList answer = new ArrayList();
        Connection connection = DriverManager.getConnection("jdbc:sqlite:db.db");
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery("SELECT * FROM " + playlist);
        while (res.next()){
            answer.add(res.getString(1));
            answer.add(res.getString(2));
            answer.add(res.getString(3));
        }
        return answer;
    }

    public static void write_db() throws SQLException{
        Connection connection = DriverManager.getConnection("jdbc:sqlite:db.db");
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery("SELECT * FROM songs");
        int len=0;
        while (res.next()) len++;
        statement.execute("drop table if exists songs");
        statement.execute("CREATE TABLE songs(name, path, author)");
        for (int i=0;i<len;i++){
            statement.execute("INSERT INTO songs VALUES('" + names.get(i) + "', '" + paths.get(i) + "', '" + authors.get(i) + "')");
        }
        statement.close();
        connection.close();
    }

    public static void make_playlist(String name) throws SQLException{
        Connection connection = DriverManager.getConnection("jdbc:sqlite:db.db");
        Statement statement = connection.createStatement();
        statement.execute("create table if not exists " + name + "(name, path, author)");
        if (check_was_playlist(name) == true) {statement.execute("INSERT INTO playlists VALUES('" + name + "')");playlists.add(name);}
        statement.close();
        connection.close();
    }

    public static void remove_playlist(String name) throws SQLException{
        Connection connection = DriverManager.getConnection("jdbc:sqlite:db.db");
        Statement statement = connection.createStatement();
        statement.execute("drop table if exists " + name);
        statement.execute("DELETE FROM playlists WHERE name='" + name + "'");
        playlists.remove(name);
        System.out.println(System.getProperty("user.dir") + "\\photos\\" + name + ".png");
        File file = new File(System.getProperty("user.dir") + "\\photos\\" + name + ".png");
        file.delete();
        file = new File(System.getProperty("user.dir") + "\\photos\\" + name + ".jpg");
        file.delete();
        statement.close();
        connection.close();
    }

    public static void add_song_to_playlist(String playlist, String name, String path, String author) throws SQLException{
        Connection connection = DriverManager.getConnection("jdbc:sqlite:db.db");
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO " + playlist + " VALUES('" + name + "', '" + path + "', '" + author + "')");
        statement.execute("INSERT INTO songs VALUES('" + name + "', '" + path + "', '" + author + "')");
        statement.close();
        connection.close();
    }

    public static void remove_song_from_playlist(String playlist, String name) throws SQLException{
        Connection connection = DriverManager.getConnection("jdbc:sqlite:db.db");
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM '" + playlist + "' WHERE name='" + name + "'");
        statement.close();
        connection.close();
    }

    public static boolean check_was_playlist(String playlist_now){
        boolean answer = true;
        for (int i=0;i<playlists.size();i++){
            if (string_checker(playlists.get(i).toString(), playlist_now)){answer = false;break;}
        }
        return answer;
    }

    public static boolean string_checker(String arg1, String arg2){
        boolean answer = false, a=false;
        if (arg1.length() == arg2.length()) {
            for (int i = 0; i < arg1.length(); i++) {
                if (arg1.charAt(i) != arg2.charAt(i)){;a=true;break;}
            }
            if (a == false) answer = true;
        }
        return answer;
    }
}


