package com.noname.Mediaplayer;

import javazoom.jl.player.Player;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;


class playlist {
	public static String path=System.getProperty("user.dir") + "/";
	public static Thread playing_track = null;
	public static int playing_mod = 0;
	public static JButton play_btn;
	public static ArrayList btns_list = new ArrayList();
	public static String current_track = null;
	public static String name_of_image;
	public static JLayeredPane layeredPane;
	public static boolean is_playing = false, now = false;
	public static Player player;
	private static JButton playng_btn, current_track_btn;
	public static String[] songs, paths, authors;
	public static JLayeredPane make_list_of_songs(String[] songs1, String[] paths1, String[] authors1, String name_of_image1, int width, int height, String playlists_name) {
		songs = songs1;
		paths = paths1;
		authors = authors1;
		name_of_image = name_of_image1;


		// Create a JLayeredPane to manage the layering of components. 
		layeredPane = new JLayeredPane();

		JLabel img = make_img(-10, 0, width*5/8, height*3/4, name_of_image);
		JPanel back = make_btn_back(0, 0, height/7, height/10);
		JPanel humber_menu = make_plus(width*9/16-16, 0, width/16, height*3/40, playlists_name, layeredPane, name_of_image);
		JPanel bottom = make_bottom_panel(-17, height*9/16, width*5/8, height*3/16);


		layeredPane.add(back, JLayeredPane.MODAL_LAYER);
		layeredPane.add(humber_menu, JLayeredPane.MODAL_LAYER);
		layeredPane.add(bottom, JLayeredPane.MODAL_LAYER);

		JPanel l = new JPanel(new GridLayout(0, 1));
		l.setBackground(new Color(0, 0, 0, 0));
		l.add(img);
		int length = songs.length;
		if (length % 2 == 1) length -= 1;
        for (int i=0;i<length;i+=2) {
			JPanel song_panel = new JPanel();
			JPanel a = null;
			if (i == 0) a = make_song(new String[]{songs[i].substring(0, songs[i].length()-4), authors[i]}, width*5/16, height*3/16, playlists_name, true);
			else a = make_song(new String[]{songs[i].substring(0, songs[i].length()-4), authors[i]}, width*5/16, height*3/16, playlists_name, false);
            song_panel.add(a);
			song_panel.add(make_song(new String[]{songs[i+1].substring(0, songs[i+1].length()-4), authors[i+1]}, width*5/16, height*3/16, playlists_name, false));
            l.add(song_panel);
        }
		if (songs.length % 2 == 1){
			JPanel song_panel = new JPanel();
			JPanel a=null;
			if (songs.length == 1) a = make_song(new String[]{songs[songs.length-1].substring(0, songs[songs.length-1].length()-4), authors[authors.length-1]}, width*5/16, height*3/16, playlists_name, true);
			else a = make_song(new String[]{songs[songs.length-1].substring(0, songs[songs.length-1].length()-4), authors[authors.length-1]}, width*5/16, height*3/16, playlists_name, false);
			song_panel.add(a);

			song_panel.add(a);

			JLabel lal = new JLabel();
			Dimension d = new Dimension(width*5/16, height*3/16);
			lal.setPreferredSize(d);
			lal.setMinimumSize(d);
			lal.setMaximumSize(d);
			song_panel.add(new JPanel().add(lal));
			l.add(song_panel);
		}

		if (songs.length == 0){
			JLabel label = new JLabel("К сожалению здесь пока нету никаких песен");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setFont(new Font("label", 0, height/25));
			l.add(label);
		}

        JScrollPane panel = new JScrollPane(l);
        panel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        panel.setBounds(-10,0, width*5/8, height*9/16);
		panel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.getVerticalScrollBar().setUnitIncrement(18);

        layeredPane.add(panel, JLayeredPane.PALETTE_LAYER);
        panel.revalidate();


		return layeredPane;
	}
	private static JPanel make_bottom_panel(int x, int y, int width, int height){
		JPanel panel = new JPanel(new GridLayout(0, 11));

		panel.setBounds(x, y, width, height);

		for (int i=0;i<4;i++) panel.add(new JLabel());

		ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\left.png").getImage().getScaledInstance(width/11, height*13/16, Image.SCALE_DEFAULT));
		JButton previous_btn = new JButton(img);
		previous_btn.setFocusPainted(false);
		previous_btn.setBackground(Color.WHITE);
		previous_btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		previous_btn.setVerticalAlignment(SwingConstants.NORTH);
		previous_btn.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (songs.length > 0 & is_playing == true) {
					int index = Settings.find_element(songs, current_track + ".mp3");

					ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\play.png").getImage().getScaledInstance(width/8, height*3/4, Image.SCALE_DEFAULT));
					JButton button = (JButton) btns_list.get(index);
					button.setIcon(img);

					index -= 1;
					if (index == -1) index = songs.length - 1;

					is_playing = false;
					player.close();
					playing_track.interrupt();

					img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\pause.png").getImage().getScaledInstance(width/8, height*3/4, Image.SCALE_DEFAULT));

					current_track = songs[index].substring(0, songs[index].length()-4);
					current_track_btn = (JButton) btns_list.get(index);
					playng_btn = (JButton) btns_list.get(index);
					playng_btn.setIcon(img);
					playing_track = new Thread() {
						public void run() {
							sound_the_song(System.getProperty("user.dir") + "\\sounds\\" + current_track + ".mp3");
						}
					};
					playing_track.start();
					is_playing = true;
				}
			}
		});
		panel.add(previous_btn);
		img=null;
		if (current_track != null) {
			if (is_playing == true)
				img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\pause.png").getImage().getScaledInstance(width/11, height*13/16, Image.SCALE_DEFAULT));
			else
				img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\play.png").getImage().getScaledInstance(width/11, height*13/16, Image.SCALE_DEFAULT));
		}
		else img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\play.png").getImage().getScaledInstance(width/11, height*13/16, Image.SCALE_DEFAULT));
		play_btn = new JButton(img);
		play_btn.setFocusPainted(false);
		play_btn.setBackground(Color.WHITE);
		play_btn.setVerticalAlignment(SwingConstants.NORTH);
		play_btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		play_btn.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {
			if (is_playing == true) try {player.close();} catch (Exception exception){}
			if (is_playing == false) {
				try {
					JButton btn_play = current_track_btn;
					playing_track = new Thread() {
						public void run() {
							sound_the_song(System.getProperty("user.dir") + "\\sounds\\" + current_track + ".mp3");
						}
					};
					playing_track.start();
					is_playing = true;
					now = true;
					ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\pause.png").getImage().getScaledInstance(width / 8, height * 13 / 16, Image.SCALE_DEFAULT));
					btn_play.setIcon(img);
					btn_play.repaint();
					playng_btn = btn_play;
					img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\pause.png").getImage().getScaledInstance(width / 11, height * 13 / 16, Image.SCALE_DEFAULT));
					play_btn.setIcon(img);
				} catch (Exception exception){}
			}
			if (is_playing == true & now == false & current_track != null){
				is_playing = false;
				player.close();
				playing_track.interrupt();
				ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\play.png").getImage().getScaledInstance(width/11, height*13/16, Image.SCALE_DEFAULT));
				play_btn.setIcon(img);
				playng_btn.setIcon(img);
				now = false;
				play_btn.repaint();
				playng_btn.repaint();
			}
			else now = false;
		}});
		panel.add(play_btn);
		img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\right.png").getImage().getScaledInstance(width/11, height*13/16, Image.SCALE_DEFAULT));
		JButton next_btn = new JButton(img);
		next_btn.setVerticalAlignment(SwingConstants.NORTH);
		next_btn.setFocusPainted(false);
		next_btn.setBackground(Color.WHITE);
		next_btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		next_btn.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (songs.length > 0 & is_playing == true) {
					int index = Settings.find_element(songs, current_track + ".mp3");

					ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\play.png").getImage().getScaledInstance(width/8, height*3/4, Image.SCALE_DEFAULT));
					JButton button = (JButton) btns_list.get(index);
					button.setIcon(img);

					index ++;
					if (index == songs.length) index = 0;

					is_playing = false;
					player.close();
					playing_track.interrupt();

					img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\pause.png").getImage().getScaledInstance(width/8, height*3/4, Image.SCALE_DEFAULT));

					current_track = songs[index].substring(0, songs[index].length()-4);
					current_track_btn = (JButton) btns_list.get(index);
					playng_btn = (JButton) btns_list.get(index);
					playng_btn.setIcon(img);
					playing_track = new Thread() {
						public void run() {
							sound_the_song(System.getProperty("user.dir") + "\\sounds\\" + current_track + ".mp3");
						}
					};
					playing_track.start();
					is_playing = true;
				}
			}
		});
		panel.add(next_btn);

		for (int i=0;i<3;i++) panel.add(new JLabel());

		img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\repeat_song.png").getImage().getScaledInstance(width/11, height*13/16, Image.SCALE_DEFAULT));
		JButton repeat = new JButton(img);
		repeat.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (is_playing){
					try {
						is_playing = false;
						player.close();
						playing_track.interrupt();
						ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\play.png").getImage().getScaledInstance(width / 11, height * 13 / 16, Image.SCALE_DEFAULT));
						playng_btn.setIcon(img);
						play_btn.setIcon(img);
					} catch (Exception exception){}
				}
				if (playing_mod == 0) {
					playing_mod = 1;
					ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\repeat_playlist.png").getImage().getScaledInstance(width/11, height*13/16, Image.SCALE_DEFAULT));
					repeat.setIcon(img);
				}
				else {
					playing_mod = 0;
					ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\repeat_song.png").getImage().getScaledInstance(width/11, height*13/16, Image.SCALE_DEFAULT));
					repeat.setIcon(img);
				}
			}
		});
		repeat.setVerticalAlignment(SwingConstants.NORTH);
		repeat.setBackground(Color.WHITE);
		repeat.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		panel.add(repeat);

		return panel;
	}
	private static JPanel make_plus(int x, int y, int width, int height, String playlists_name, JLayeredPane layeredPane, String name_of_image){
		JPanel panel = new JPanel(new GridLayout(1, 0));

		panel.setBackground(Color.WHITE);
		panel.setBounds(x, y, width, height);

		ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\plus.png").getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
		JButton btn = new JButton(img);
		btn.setFocusable(false);
		btn.setBackground(Color.white);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		final String[] img_path_list = new String[1];
		final String[] names = new String[1];
		btn.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser j = new JFileChooser();
				j.setFileFilter(new FileNameExtensionFilter("Songs", "mp3"));
				int r = j.showOpenDialog(null);
				if (r == JFileChooser.APPROVE_OPTION) {
					img_path_list[0] = j.getSelectedFile().getAbsolutePath();
					names[0] = j.getSelectedFile().getName();
					System.out.println("ok");
					try {
						if (check_was_it_in_songs(names[0]) == false) {
							Database.add_song_to_playlist(playlists_name, names[0], img_path_list[0], "Noname");
							String[] a = authors;
							String[] b = songs;
							String[] c = paths;
							paths = new String[paths.length + 1];
							songs = new String[songs.length + 1];
							authors = new String[authors.length + 1];
							for (int i = 0; i < a.length; i++) {
								paths[i] = c[i];
								songs[i] = b[i];
								authors[i] = a[i];
							}
							paths[c.length] = img_path_list[0];
							songs[b.length] = names[0];
							authors[a.length] = "Noname";
							JLayeredPane n = layeredPane;
							reload_songs_list(songs, paths, authors, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height,  name_of_image, playlists_name, n);
						}
					} catch (SQLException ex) {ex.printStackTrace();}
				}
			}});

		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.add(btn);

		return panel;
	};
	public static void reload_songs_list(String[] songs1, String[] paths1, String[] authors1, int width, int height, String name_of_image, String playlists_name, JLayeredPane layeredPane){
		layeredPane.removeAll();

		songs = songs1;
		paths = paths1;
		authors = authors1;
		// Create a JLayeredPane to manage the layering of components.

		JLabel img = make_img(-10, 0, width*5/8, height*3/4, name_of_image);
		JPanel back = make_btn_back(0, 0, height/7, height/10);
		JPanel humber_menu = make_plus(width*9/16-16, 0, width/16, height*3/40, playlists_name, layeredPane, name_of_image);
		JPanel bottom = make_bottom_panel(-17, height*9/16, width*5/8, height*3/16);


		layeredPane.add(back, JLayeredPane.MODAL_LAYER);
		layeredPane.add(humber_menu, JLayeredPane.MODAL_LAYER);
		layeredPane.add(bottom, JLayeredPane.MODAL_LAYER);

		JPanel l = new JPanel(new GridLayout(0, 1));
		l.setBackground(new Color(0, 0, 0, 0));
		l.add(img);
		int length = songs.length;
		if (length % 2 == 1) length -= 1;
		for (int i=0;i<length;i+=2) {
			JPanel song_panel = new JPanel();
			song_panel.add(make_song(new String[]{songs[i].substring(0, songs[i].length()-4), authors[i]}, width*5/16, height*3/16, playlists_name, false));
			song_panel.add(make_song(new String[]{songs[i+1].substring(0, songs[i+1].length()-4), authors[i+1]}, width*5/16, height*3/16, playlists_name, false));
			l.add(song_panel);
		}
		if (songs.length % 2 == 1){
			JPanel song_panel = new JPanel();
			song_panel.add(make_song(new String[]{songs[songs.length-1].substring(0, songs[songs.length-1].length()-4), authors[authors.length-1]}, width*5/16, height*3/16, playlists_name, false));
			JLabel lal = new JLabel();
			Dimension d = new Dimension(width*5/16, height*3/16);
			lal.setPreferredSize(d);
			lal.setMinimumSize(d);
			lal.setMaximumSize(d);
			song_panel.add(new JPanel().add(lal));
			l.add(song_panel);
		}

		if (songs.length == 0){
			JLabel label = new JLabel("К сожалению здесь пока нету никаких песен");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setFont(new Font("label", 0, height/25));
			l.add(label);
		}

		JScrollPane panel = new JScrollPane(l);
		panel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.setBounds(-10,0, width*5/8, height*9/16);
		panel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		panel.getVerticalScrollBar().setUnitIncrement(18);

		layeredPane.add(panel, JLayeredPane.PALETTE_LAYER);
		panel.revalidate();

		layeredPane.revalidate();
		layeredPane.repaint();
	}
	private static JPanel make_song(String[] song, int width, int height, String playlists_name, boolean is_first){
		JPanel panel = new JPanel();
		Dimension d = new Dimension(width, height);
		panel.setPreferredSize(d);
		panel.setMaximumSize(d);
		panel.setMinimumSize(d);
		ImageIcon img=null;
		if (current_track != null) {
			if (is_playing == true & Settings.string_checker(song[0], current_track))
				img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\pause.png").getImage().getScaledInstance(width / 4, height / 2, Image.SCALE_DEFAULT));
			else
				img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\play.png").getImage().getScaledInstance(width / 4, height / 2, Image.SCALE_DEFAULT));
		}
		else img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\play.png").getImage().getScaledInstance(width / 4, height / 2, Image.SCALE_DEFAULT));
		d = new Dimension(width/4, height-10);
		JButton btn_play = new JButton(img);
		btn_play.setFocusPainted(false);
		btn_play.setMinimumSize(d);
		btn_play.setMaximumSize(d);
		btn_play.setPreferredSize(d);
		btn_play.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn_play.setAction(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (is_playing == false) {
					current_track_btn = btn_play;
					current_track = song[0];
					playing_track = new Thread() {;public void run() {sound_the_song(System.getProperty("user.dir") + "\\sounds\\" + song[0] + ".mp3");}};
					playing_track.start();
					is_playing = true;
					now = true;
					ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\pause.png").getImage().getScaledInstance(width/4, height/2, Image.SCALE_DEFAULT));
					btn_play.setIcon(img);
					btn_play.repaint();
					playng_btn = btn_play;
					img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\pause.png").getImage().getScaledInstance(width/4, height*3/4, Image.SCALE_DEFAULT));
					play_btn.setIcon(img);
				}
				if (is_playing == true & now == false) {
					is_playing = false;
					player.close();
					playing_track.interrupt();
					if (btn_play.getIcon().hashCode() != playng_btn.getIcon().hashCode()){
						ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\play.png").getImage().getScaledInstance(width / 4, height / 2, Image.SCALE_DEFAULT));
						playng_btn.setIcon(img);
						playing_track = new Thread() {;public void run() {sound_the_song(System.getProperty("user.dir") + "\\sounds\\" + song[0] + ".mp3");}};
						playing_track.start();
						is_playing = true;
						img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\pause.png").getImage().getScaledInstance(width / 4, height / 2, Image.SCALE_DEFAULT));
						btn_play.setIcon(img);
						playng_btn = btn_play;
						current_track = song[0];
						current_track_btn = btn_play;
					}
					else{
						ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\play.png").getImage().getScaledInstance(width / 4, height / 2, Image.SCALE_DEFAULT));
						playng_btn.setIcon(img);
						img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\play.png").getImage().getScaledInstance(width / 4, height*3/4, Image.SCALE_DEFAULT));
						play_btn.setIcon(img);
					}
				}
				else now = false;
			}
		});
		btn_play.setIcon(img);

		if (is_first) {current_track_btn = btn_play;current_track = song[0];}

		btns_list.add(btn_play);

		JPanel p = new JPanel();
		p.add(BorderLayout.WEST, btn_play);
		p.setBounds(0, 0, width/4, height);
		JLabel l = new JLabel(song[0] + " - " + song[1]);
		l.setBounds(width/4, 0, width*3/4, height);
		JPanel p2 = new JPanel();
		l.setBounds(width/4, 0, width*27/40, height);

		img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\delete.png").getImage().getScaledInstance(width/10, height/2, Image.SCALE_DEFAULT));
		JButton delete = new JButton(img);
		delete.setBackground(Color.RED);
		delete.setFocusPainted(false);
		delete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		delete.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {remove_song(playlists_name, song[0]);}});
		p2.add(l);
		p2.add(delete);

		panel.add(BorderLayout.WEST, p);
		panel.add(BorderLayout.EAST, p2);
		return panel;
	}
	private static void remove_song(String playlists_name, String name){
		try {
			is_playing = false;
			player.close();
			playing_track.interrupt();
			name = name + ".mp3";
			Database.remove_song_from_playlist(playlists_name, name);
			int index = -1;
			for (int i=0;i<songs.length;i++){
				if (Settings.string_checker(name, songs[i]) == true) {index=i;break;}
			}
			ArrayList help1 = new ArrayList(), help2 = new ArrayList(), help3 = new ArrayList();
			for (int i=0;i<songs.length;i++){
				help1.add(songs[i]);
				help2.add(paths[i]);
				help3.add(authors[i]);
			}
			help1.remove(songs[index]);
			help2.remove(paths[index]);
			help3.remove(authors[index]);
			songs = new String[songs.length-1];
			paths = new String[paths.length-1];
			authors = new String[authors.length-1];
			for (int i=0;i<help1.size();i++){
				songs[i] = help1.get(i).toString();
				paths[i] = help2.get(i).toString();
				authors[i] = help3.get(i).toString();
			}
			if (songs.length > 0) {
				current_track = songs[0].substring(0, songs[0].length() - 4);
				current_track_btn = (JButton) btns_list.get(0);
				playng_btn = (JButton) btns_list.get(0);
				play_btn = (JButton) btns_list.get(0);
			}
			else {
				current_track_btn = null;
				current_track = null;
			}
		} catch (Exception e) {e.printStackTrace();}
		reload_songs_list(songs, paths, authors, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, name_of_image, playlists_name, layeredPane);
	}
	private static void sound_the_song(String path){
		try{
			FileInputStream in = new FileInputStream(path);
			player = new Player(in);
			player.play();
			if (is_playing == true) {
				if (playing_mod == 1) {
					int index = Settings.find_element(songs, current_track + ".mp3");
					while (is_playing == true) {
						int width = Toolkit.getDefaultToolkit().getScreenSize().width, height = Toolkit.getDefaultToolkit().getScreenSize().height;
						ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\play.png").getImage().getScaledInstance(width*5/64, height*4/32-10, Image.SCALE_DEFAULT));

						JButton button = (JButton) btns_list.get(index);
						button.setIcon(img);

						index ++;
						if (index == songs.length) index = 0;

						img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\pause.png").getImage().getScaledInstance(width*5/64, height*4/32-10, Image.SCALE_DEFAULT));
						button = (JButton) btns_list.get(index);
						button.setIcon(img);
						playng_btn = button;
						current_track_btn = button;
						current_track = songs[index].substring(0, songs[index].length()-4);
						path = System.getProperty("user.dir") + "\\sounds\\" + songs[index];
						in = new FileInputStream(path);
						player = new Player(in);
						player.play();
					}
				} else {
					while (is_playing == true) {
						in = new FileInputStream(path);
						player = new Player(in);
						player.play();
					}
				}
			}
		} catch (Exception e){}
	}

	private static JPanel make_btn_back(int x, int y, int width, int height) {
		// Create a colored JPanel with specified color and position. 
		JPanel panel = new JPanel(new GridLayout(1, 0));
		panel.setBounds(x, y, width, height);
		panel.setBackground(new Color(0, 0, 0, 255));
		panel.setBackground(new Color(0, 0, 0, 0));
		ImageIcon i = new ImageIcon(path + "photos/back.png");
		Image img = i.getImage().getScaledInstance(width*3/4, height-10, Image.SCALE_DEFAULT);
		JButton lb = new JButton(new ImageIcon(img));
		Dimension d = new Dimension(width*3/4, height-10);
		lb.setMinimumSize(d);
		lb.setPreferredSize(d);
		lb.setMaximumSize(d);
		lb.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {MediaPlayer.back_to_main_menu();is_playing = false; player.close(); now = false;}});
		lb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lb.setFocusPainted(false);
		lb.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.add(lb);
		panel.setBackground(Color.WHITE);
		return panel; 
	}

	private static JLabel make_img(int x, int y, int width, int height, String name){
		ImageIcon i = new ImageIcon(name);
		Image img = i.getImage().getScaledInstance(width, height/2, Image.SCALE_DEFAULT);
		JLabel label = new JLabel(new ImageIcon(img));
		Dimension d = new Dimension(width, height/4);
		label.setMinimumSize(d);
		label.setPreferredSize(d);
		label.setMaximumSize(d);
		return label;
	}

	private static boolean check_was_it_in_songs(String song){
		boolean answer = false;

		for (int i=0;i<songs.length;i++){
			if (Settings.string_checker(song, songs[i]) == true) {answer = true;break;}
		}

		return answer;
	}
} 
