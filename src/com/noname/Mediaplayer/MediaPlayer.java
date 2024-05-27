package com.noname.Mediaplayer;

import com.sun.tools.javac.Main;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.xml.stream.Location;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.ImageObserver;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;

public class MediaPlayer {
    public static playlist playlist_tool = new playlist();
    public static JFrame frame;
    public static Making_playlist window;
    public static String path=System.getProperty("user.dir") + "/";
    public static Database db = new Database();
    public static MainMenu mainmenu = new MainMenu();
    public static int width = Toolkit.getDefaultToolkit().getScreenSize().width, height=Toolkit.getDefaultToolkit().getScreenSize().height;
    public static boolean is_making_playlist = false;
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        db.main(args);

        frame = new JFrame("Media player");
        frame.setIconImage(new ImageIcon(path + "photos/icon.png").getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(width*5/8, height*3/4);
        frame.setLocationRelativeTo(null);

        JLayeredPane pane = mainmenu.menu(path, db.playlists, width*5/8, height*3/4);

        frame.setContentPane(pane);

        frame.setVisible(true);
    }

    public static void open_playlist(String name) throws SQLException {
        ArrayList playlists_data = db.read_playlist(name);
        ArrayList songs = new ArrayList();
        ArrayList paths = new ArrayList();
        ArrayList authors = new ArrayList();

        for (int i=0;i<playlists_data.size()/3;i++){
            songs.add(playlists_data.get(i*3));
            paths.add(playlists_data.get(i*3+1));
            authors.add(playlists_data.get(i*3+2));
        }

        String path = System.getProperty("user.dir") + "\\photos\\" + name + ".png";
        boolean is_got = false;
        try {
            new FileInputStream(path);
            is_got = true;
        } catch (IOException e){path = System.getProperty("user.dir") + "\\photos\\" + name + ".jpg";}
        try {
            if (is_got == false){
                new FileInputStream(path);
            }
        } catch (IOException e){e.printStackTrace();}

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

        String[] songs_an = new String[songs.size()];
        String[] paths_an = new String[paths.size()];
        String[] authors_an = new String[authors.size()];

        for (int i=0;i<songs.size();i++){
            songs_an[i] = songs.get(i).toString();
            paths_an[i] = paths.get(i).toString();
            authors_an[i] = authors.get(i).toString();
        }

        JLayeredPane panel = playlist.make_list_of_songs(songs_an, paths_an, authors_an, path, size.width, size.height, name);

        JFrame a = new JFrame("Media player");
        a.setIconImage(new ImageIcon(System.getProperty("user.dir") + "\\photos\\icon.png").getImage());
        a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        a.setResizable(false);
        a.setSize(width*5/8, height*3/4);
        a.setLocationRelativeTo(null);

        frame.dispose();

        frame = a;

        frame.setVisible(true);

        frame.add(panel);

        frame.setVisible(true);
    }

    public static void back_to_main_menu(){
        JLayeredPane pane = mainmenu.menu(path, db.playlists, width*5/8, height*3/4);

        frame.setContentPane(pane);

        frame.setVisible(true);
    }

    public static void delete_playlist(String playlist){
        JFrame a = new JFrame("Media player");
        a.setIconImage(new ImageIcon(path + "\\photos\\icon.png").getImage());
        a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        a.setResizable(false);
        a.setSize(width*5/8, height*3/4);
        a.setLocationRelativeTo(null);
        frame.setVisible(false);
        frame = a;
        frame.setVisible(true);
        try {
            db.remove_playlist(playlist);
        } catch (Exception e) {e.printStackTrace();}
        back_to_main_menu();
    }

    public static void make_playlist(){
        if (is_making_playlist == false){
            is_making_playlist = true;
            window = new Making_playlist();
            window.setVisible(true);
        }
    }
}

class Making_playlist extends JFrame{
    Making_playlist(){
        int width = Toolkit.getDefaultToolkit().getScreenSize().width, height = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setSize(width/2, height/2);
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(System.getProperty("user.dir") + "\\photos\\plus.png").getImage());
        this.setTitle("Making playlist");

        createGUI();
    }

    public static final Font FONT = new Font("Verdana", Font.PLAIN, 11);

    public void createGUI() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent event) {}
            public void windowClosed(WindowEvent event) {}
            public void windowClosing(WindowEvent event) {MediaPlayer.is_making_playlist = false;}
            public void windowDeactivated(WindowEvent event) {}
            public void windowDeiconified(WindowEvent event) {}
            public void windowIconified(WindowEvent event) {}
            public void windowOpened(WindowEvent event) {}

        });

        JPanel panel = new JPanel(new GridLayout(5, 0));

        JTextField field = new JTextField();
        field.setDocument(new JTextFieldLimit(17));
        field.setFont(new Font("textfield", 0, Toolkit.getDefaultToolkit().getScreenSize().height*7/100));

        panel.add(new JLabel());
        panel.add(field);

        final String[] img_path_list = {null};

        JButton fileChooser_btn = new JButton("Выбрать обложку плейлиста");
        fileChooser_btn.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {
            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            j.setFileFilter(new FileNameExtensionFilter("Image", "png", "jpg"));
            int r = j.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                img_path_list[0] = j.getSelectedFile().getAbsolutePath();
            }
        }});
        fileChooser_btn.setFocusPainted(false);

        panel.add(fileChooser_btn);

        JButton btn = new JButton("Подтвердить");
        btn.setBackground(Color.GREEN);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.black));

        btn.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {if (check_was_it_in_playlists(Database.playlists, field.getText()) == false){
            try {
                Database.make_playlist(field.getText());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            if (img_path_list[0] == null){
                Path source_path = Paths.get(System.getProperty("user.dir") + "\\photos\\pain_holst.png");
                Path dest_path = Paths.get(System.getProperty("user.dir") + "\\photos\\" + field.getText() + ".png");

                try {
                    Files.copy(source_path, dest_path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            else {
                Path source_path = Paths.get(img_path_list[0]);
                Path dest_path = Paths.get(System.getProperty("user.dir") + "\\photos\\" + field.getText() + img_path_list[0].substring(img_path_list[0].length()-4, img_path_list[0].length()));

                try {
                    Files.copy(source_path, dest_path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            int width = Toolkit.getDefaultToolkit().getScreenSize().width, height = Toolkit.getDefaultToolkit().getScreenSize().height;
            MediaPlayer.window.dispose();
            JFrame a = new JFrame("Media player");
            a.setIconImage(new ImageIcon(System.getProperty("user.dir") + "\\photos\\icon.png").getImage());
            a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            a.setResizable(false);
            a.setSize(width*5/8, height*3/4);
            a.setLocationRelativeTo(null);
            MediaPlayer.frame.setVisible(false);
            MediaPlayer.frame = a;
            MediaPlayer.frame.setVisible(true);
            MediaPlayer.back_to_main_menu();
            MediaPlayer.is_making_playlist = false;
        }}});
        panel.add(btn);

        this.add(panel);
    }

    public static boolean check_was_it_in_playlists(ArrayList playlists, String name){
        boolean answer = false;

        for (int i=0;i<playlists.size();i++){
            if (Settings.string_checker(playlists.get(i).toString().toLowerCase(), name.toLowerCase())){answer = true; break;}
        }

        if (Settings.string_checker(name, "") == true) answer = true;
        if (name == null) answer = true;

        return answer;
    }
}