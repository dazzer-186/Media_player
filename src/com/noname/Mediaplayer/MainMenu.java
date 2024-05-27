package com.noname.Mediaplayer;

import org.w3c.dom.Text;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.Style;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

public class MainMenu {
    public static Settings settings = new Settings();
    public static JLayeredPane menu(String path, ArrayList playlists, int width, int height){
        JLayeredPane panel = new JLayeredPane();
        panel.add(make_top_panel(path, width, height/10, panel, playlists));
        JScrollPane pane = make_playlists(playlists, width, height);
        panel.add(pane);
        return panel;
    }

    public static JPanel make_top_panel(String path, int width, int height, JLayeredPane p, ArrayList playlists){
        JPanel panel = new JPanel();
        Image img = new ImageIcon(path + "photos/search.png").getImage().getScaledInstance(width/10, height*9/10, Image.SCALE_DEFAULT);
        JButton btn = new JButton(new ImageIcon(img));
        btn.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {open_search(panel, p, playlists);}});
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBackground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusable(false);
        btn.setFocusPainted(false);
        panel.add(BorderLayout.PAGE_START, btn);
        panel.setBackground(Color.WHITE);
        panel.setBounds(0, 0, width, height);
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.setBorder(null);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {open_search(panel, p, playlists);super.mouseClicked(e);}});
        return panel;
    };

    public static void close_search(JPanel panel, JLayeredPane p, ArrayList playlists, JTextField field){
        String path = System.getProperty("user.dir") + "/";

        int width = panel.getSize().width, height = panel.getSize().height;

        String filter = field.getText();

        panel.removeAll();

        p.removeAll();

        Image img = new ImageIcon(path + "photos/search.png").getImage().getScaledInstance(width/10, height*9/10, Image.SCALE_DEFAULT);
        JLabel btn = new JLabel(new ImageIcon(img));
        btn.setBackground(new Color(0, 0, 0, 0));
        panel.add(BorderLayout.PAGE_START, btn);

        panel.setBorder(BorderFactory.createLineBorder(Color.black));

        panel.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {open_search(panel, p, playlists);super.mouseClicked(e);}});
        btn.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {open_search(panel, p, playlists);super.mouseClicked(e);}});

        panel.revalidate();
        panel.repaint();

        p.add(panel);

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        p.add(make_playlists(filter_playlists(playlists, filter), size.width*5/8, Toolkit.getDefaultToolkit().getScreenSize().height*3/4));
    }

    public static ArrayList filter_playlists(ArrayList playlists, String filter){
        ArrayList answer = new ArrayList();
        if (settings.string_checker(filter, "")){
            answer = playlists;
        }
        else{
            for (int i = 0; i < playlists.size(); i++) {
                if (playlists.get(i).toString().length() >= filter.length() & filter.length() > 0) {
                    if (filter.length() > 1) {
                        if (settings.string_checker(filter.toLowerCase(), playlists.get(i).toString().substring(0, filter.length()).toLowerCase()))
                            answer.add(playlists.get(i));
                    } else {
                        if (filter.toLowerCase().charAt(0) == playlists.get(i).toString().toLowerCase().charAt(0))
                            answer.add(playlists.get(i));
                    }
                }
            }
        }
        return answer;
    }
    public static JScrollPane make_playlists(ArrayList playlists, int width, int height){
        JPanel main_panel = new JPanel(new GridLayout(0, 1));
        Dimension playlists_size = new Dimension(width/3, height*2/5);
        Dimension string_size = new Dimension(width, height*2/5);
        for (int string=0;string<playlists.size()/3;string++){
            String[] names = new String[4];
            ImageIcon[] images = new ImageIcon[4];
            for (int colum=0;colum<3;colum++){
                String name = playlists.get(string*3+colum).toString();
                names[colum] = name;
                String path = System.getProperty("user.dir") + "\\photos\\" + name + ".png";
                boolean is_was = false;
                try {
                    new FileInputStream(path);
                    ImageIcon img = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(playlists_size.width, playlists_size.height, Image.SCALE_DEFAULT));
                    images[colum] = img;
                    is_was = true;
                } catch (Exception e) {path = System.getProperty("user.dir") + "\\photos" + name + ".jpg";}
                try {
                    if (is_was == false) {
                        new FileInputStream(path);
                        ImageIcon img = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(playlists_size.width, playlists_size.height, Image.SCALE_DEFAULT));
                        images[colum] = img;
                    }
                } catch (Exception e) {e.printStackTrace();}
            }
            Make_three_playlist p = new Make_three_playlist(images, names, playlists_size.width, playlists_size.height, string_size.width, string_size.height);
            main_panel.add(p);
        }

        if (playlists.size() % 3 == 2){
            String name = playlists.get(playlists.size()-2).toString();
            String path = System.getProperty("user.dir") + "\\photos\\" + name + ".png";
            ImageIcon img = null;
            String[] names = new String[2];
            ImageIcon[] images = new ImageIcon[2];
            boolean is_was = false;
            try {
                new FileInputStream(path);
                img = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(playlists_size.width, playlists_size.height, Image.SCALE_DEFAULT));
                is_was = true;
            } catch (Exception e) {path = System.getProperty("user.dir") + "\\photos\\" + name + ".jpg";}
            try {
                if (is_was == false){
                    new FileInputStream(path);
                    img = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(playlists_size.width, playlists_size.height, Image.SCALE_DEFAULT));
                }
            } catch (Exception e){e.printStackTrace();}
            names[0] = name;
            images[0] = img;
            name = playlists.get(playlists.size()-1).toString();
            path = System.getProperty("user.dir") + "\\photos\\" + name + ".png";
            img = null;
            is_was = false;
            try {
                new FileInputStream(path);
                img = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(playlists_size.width, playlists_size.height, Image.SCALE_DEFAULT));
                is_was = true;
            } catch (Exception e) {path = System.getProperty("user.dir") + "\\photos\\" + name + ".jpg";}
            try {
                if (is_was == false){
                    new FileInputStream(path);
                    img = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(playlists_size.width, playlists_size.height, Image.SCALE_DEFAULT));
                }
            } catch (Exception e){e.printStackTrace();}
            names[1] = name;
            images[1] = img;
            main_panel.add(new Make_two_playlist(images, names, playlists_size.width, playlists_size.height, string_size.width, string_size.height));
        }
        if (playlists.size() % 3 == 1){
            String name = playlists.get(playlists.size()-1).toString();
            String path = System.getProperty("user.dir") + "\\photos\\" + name + ".png";
            ImageIcon img = null;
            boolean is_was = false;
            try {
                new FileInputStream(path);
                img = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(playlists_size.width, playlists_size.height, Image.SCALE_DEFAULT));
                is_was = true;
            } catch (Exception e) {path = System.getProperty("user.dir") + "\\photos\\" + name + ".jpg";}
            try {
                if (is_was == false){
                    new FileInputStream(path);
                    img = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(playlists_size.width, playlists_size.height, Image.SCALE_DEFAULT));
                }
            } catch (Exception e){e.printStackTrace();}
            Make_one_playlist p = new Make_one_playlist(img, name, playlists_size.width, playlists_size.height, string_size.width, string_size.height);
            main_panel.add(p);
        }

        ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\plus.png").getImage().getScaledInstance(162, 162, Image.SCALE_DEFAULT));
        JButton add_btn = new JButton("Добавить", img);
        add_btn.setBackground(Color.green);
        add_btn.setHorizontalTextPosition(SwingConstants.LEFT);
        add_btn.setVerticalAlignment(SwingConstants.CENTER);
        add_btn.setFocusPainted(false);
        add_btn.setFont(new Font("add_btn", 0, height/10));
        add_btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add_btn.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {MediaPlayer.make_playlist();}});
        main_panel.add(add_btn);

        JPanel pix = new JPanel();
        Dimension d = new Dimension(width, 0);
        pix.setPreferredSize(d);
        pix.setMaximumSize(d);
        pix.setMinimumSize(d);

        main_panel.add(pix);

        JScrollPane panel = new JScrollPane(main_panel);

        panel.getVerticalScrollBar().setUnitIncrement(18);
        panel.setBounds(0, height/10, width, height*9/10);
        panel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        return panel;
    }

    public static void open_search(JPanel panel, JLayeredPane pane, ArrayList playlists){
        Dimension d = panel.getSize();

        panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        panel.removeAll();

        JTextField field = new JTextField();
        Dimension field_dimension = new Dimension(d.width*17/20, d.height);
        field.setPreferredSize(field_dimension);
        field.setMinimumSize(field_dimension);
        field.setMaximumSize(field_dimension);
        field.setToolTipText("Введите текст для поиска");
        field.setDisabledTextColor(Color.gray);
        field.setFont(new Font("textfield", 0, d.height*7/10));
        field.setDocument(new JTextFieldLimit(30));
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {if (e.getKeyChar() == '\n') close_search(panel, pane, playlists, field);super.keyTyped(e);}
        });

        JButton btn = new JButton(new ImageIcon(new ImageIcon(System.getProperty("user.dir")+"\\photos\\search.png").getImage().getScaledInstance(d.width/11, d.height, Image.SCALE_DEFAULT)));
        btn.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {close_search(panel, pane, playlists, field);}});
        Dimension btn_dimension = new Dimension(d.width/11, d.height);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(btn_dimension);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMinimumSize(btn_dimension);
        btn.setMaximumSize(btn_dimension);


        JPanel p = new JPanel();
        p.add(BorderLayout.PAGE_START, field);
        p.add(BorderLayout.PAGE_START, btn);
        p.setBackground(Color.WHITE);

        panel.add(p);

        panel.revalidate();
        panel.repaint();
    }
}

class JTextFieldLimit extends PlainDocument {
    private int limit;
    JTextFieldLimit(int limit) {
        super();
        this.limit = limit;
    }
    JTextFieldLimit(int limit, boolean upper) {
        super();
        this.limit = limit;
    }
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null)
            return;
        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);
        }
    }
}

class Make_three_playlist extends JPanel {
    private JLayeredPane layeredPane1 = new JLayeredPane();
    private JLayeredPane layeredPane2 = new JLayeredPane();
    private JLayeredPane layeredPane3 = new JLayeredPane();

    Make_three_playlist(ImageIcon[] image, String[] name, int width, int height, int width_all, int height_all) {
        super();

        Dimension d = new Dimension(width_all, height_all);
        this.setPreferredSize(d);
        this.setMinimumSize(d);
        this.setMaximumSize(d);

        layeredPane1.setBounds(0, 0, width, height);
        layeredPane2.setBounds(width, 0, width, height);
        layeredPane3.setBounds(width*2, 0, width, height);

        JLabel imageContainer = new JLabel(), info = new JLabel();
        imageContainer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        info.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageContainer.setIcon(image[0]);
        info.setBounds( 0, height*7/10,  width, height/5);
        imageContainer.setBounds( 0, 0, width, height);
        info.setText(name[0]);
        info.setFont(new Font("label", 0, height/5));
        info.setVerticalAlignment(0);
        info.setHorizontalAlignment(0);
        imageContainer.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {super.mouseClicked(e);
            try {
                MediaPlayer.open_playlist(name[0]);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }});
        info.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {super.mouseClicked(e);
            try {
                MediaPlayer.open_playlist(name[0]);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }});
        layeredPane1.setPreferredSize(new Dimension(300, 300));
        layeredPane1.add(imageContainer, new Integer(50));
        layeredPane1.add(info, new Integer(100));
        this.add(layeredPane1);
        ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\delete.png").getImage().getScaledInstance(width/7, width/7, Image.SCALE_DEFAULT));
        JButton delete = new JButton(img);
        delete.setBackground(Color.red);
        d = new Dimension(width/7, height);
        delete.setPreferredSize(d);
        delete.setMaximumSize(d);
        delete.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {MediaPlayer.delete_playlist(name[0]);}});
        delete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        delete.setFocusPainted(false);
        this.add(BorderLayout.SOUTH, delete);
        // CHANGED CODE
        // Manually set layout the components.




        imageContainer = new JLabel();
        info = new JLabel();
        imageContainer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        info.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageContainer.setIcon(image[1]);
        info.setBounds( 0, height*7/10,  width, height/5);
        imageContainer.setBounds( 0, 0, width, height);
        info.setText(name[1]);
        info.setFont(new Font("label", 0, height/5));
        info.setVerticalAlignment(0);
        info.setHorizontalAlignment(0);
        imageContainer.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {super.mouseClicked(e);
            try {
                MediaPlayer.open_playlist(name[1]);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }});
        info.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {super.mouseClicked(e);
            try {
                MediaPlayer.open_playlist(name[1]);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }});
        layeredPane2.setPreferredSize(new Dimension(300, 300));
        layeredPane2.add(imageContainer, new Integer(50));
        layeredPane2.add(info, new Integer(100));
        this.add(layeredPane2);
        img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\delete.png").getImage().getScaledInstance(width/7, width/7, Image.SCALE_DEFAULT));
        delete = new JButton(img);
        delete.setBackground(Color.red);
        d = new Dimension(width/7, height);
        delete.setPreferredSize(d);
        delete.setMaximumSize(d);
        delete.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {MediaPlayer.delete_playlist(name[1]);}});
        delete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        delete.setFocusPainted(false);
        this.add(BorderLayout.SOUTH, delete);
        // CHANGED CODE
        // Manually set layout the components.




        imageContainer = new JLabel();
        info = new JLabel();
        imageContainer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        info.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageContainer.setIcon(image[2]);
        info.setBounds( 0, height*7/10,  width, height/5);
        imageContainer.setBounds( 0, 0, width, height);
        info.setText(name[2]);
        info.setFont(new Font("label", 0, height/5));
        info.setVerticalAlignment(0);
        info.setHorizontalAlignment(0);
        imageContainer.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {super.mouseClicked(e);
            try {
                MediaPlayer.open_playlist(name[2]);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }});
        info.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {super.mouseClicked(e);
            try {
                MediaPlayer.open_playlist(name[2]);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }});
        layeredPane3.setPreferredSize(new Dimension(300, 300));
        layeredPane3.add(imageContainer, new Integer(50));
        layeredPane3.add(info, new Integer(100));
        this.add(layeredPane3);
        img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\delete.png").getImage().getScaledInstance(width/7, width/7, Image.SCALE_DEFAULT));
        delete = new JButton(img);
        delete.setBackground(Color.red);
        d = new Dimension(width/7, height);
        delete.setPreferredSize(d);
        delete.setMaximumSize(d);
        delete.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {MediaPlayer.delete_playlist(name[2]);}});
        delete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        delete.setFocusPainted(false);
        this.add(BorderLayout.SOUTH, delete);
        // CHANGED CODE
        // Manually set layout the components.
    }
}

class Make_two_playlist extends JPanel {
    private JLayeredPane layeredPane1 = new JLayeredPane();
    private JLayeredPane layeredPane2 = new JLayeredPane();

    Make_two_playlist(ImageIcon[] image, String[] name, int width, int height, int width_all, int height_all) {
        super();

        Dimension d = new Dimension(width_all, height_all);
        this.setPreferredSize(d);
        this.setMinimumSize(d);
        this.setMaximumSize(d);

        layeredPane1.setBounds(0, 0, width, height);
        layeredPane2.setBounds(width, 0, width, height);

        JLabel imageContainer = new JLabel(), info = new JLabel();
        imageContainer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        info.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageContainer.setIcon(image[0]);
        info.setBounds( 0, height*7/10,  width, height/5);
        layeredPane1.setPreferredSize(new Dimension(300, 300));
        layeredPane1.add(imageContainer, new Integer(50));
        layeredPane1.add(info, new Integer(100));
        this.add(layeredPane1);
        // CHANGED CODE
        // Manually set layout the components.
        imageContainer.setBounds( 0, 0, width, height);
        info.setText(name[0]);
        info.setFont(new Font("label", 0, height/5));
        info.setVerticalAlignment(0);
        info.setHorizontalAlignment(0);
        imageContainer.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {super.mouseClicked(e);try {
                MediaPlayer.open_playlist(name[0]);
            } catch (SQLException ex) {throw new RuntimeException(ex);}}});
        info.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {super.mouseClicked(e);try {
                MediaPlayer.open_playlist(name[0]);
            } catch (SQLException ex) {throw new RuntimeException(ex);}}});
        ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\delete.png").getImage().getScaledInstance(width/7, width/7, Image.SCALE_DEFAULT));
        JButton delete = new JButton(img);
        delete.setBackground(Color.red);
        d = new Dimension(width/7, height);
        delete.setPreferredSize(d);
        delete.setMaximumSize(d);
        delete.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {MediaPlayer.delete_playlist(name[0]);}});
        delete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        delete.setFocusPainted(false);
        this.add(BorderLayout.SOUTH, delete);




        imageContainer = new JLabel();
        info = new JLabel();
        imageContainer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        info.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageContainer.setIcon(image[1]);
        info.setBounds( 0, height*7/10,  width, height/5);
        layeredPane2.setPreferredSize(new Dimension(300, 300));
        layeredPane2.add(imageContainer, new Integer(50));
        layeredPane2.add(info, new Integer(100));
        this.add(layeredPane2);
        // CHANGED CODE
        // Manually set layout the components.
        imageContainer.setBounds( 0, 0, width, height);
        info.setText(name[1]);
        info.setFont(new Font("label", 0, height/5));
        info.setVerticalAlignment(0);
        info.setHorizontalAlignment(0);
        imageContainer.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {super.mouseClicked(e);
            try {
                MediaPlayer.open_playlist(name[1]);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }});
        info.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {super.mouseClicked(e);
            try {
                MediaPlayer.open_playlist(name[1]);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }});
        img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\delete.png").getImage().getScaledInstance(width/7, width/7, Image.SCALE_DEFAULT));
        delete = new JButton(img);
        delete.setBackground(Color.red);
        d = new Dimension(width/7, height);
        delete.setPreferredSize(d);
        delete.setMaximumSize(d);
        delete.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {MediaPlayer.delete_playlist(name[1]);}});
        delete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        delete.setFocusPainted(false);
        this.add(BorderLayout.SOUTH, delete);
    }
}

class Make_one_playlist extends JPanel {
    private JLayeredPane layeredPane;
    private JLabel imageContainer = new JLabel();
    private JLabel info = new JLabel();

    Make_one_playlist(ImageIcon image, String name, int width, int height, int width_all, int height_all) {
        super();

        this.imageContainer.setIcon(image);

        this.layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, width, height);
        this.setBounds(0, 0, width, height);
        info.setBounds( 0, height*7/10,  width, height/5);
        imageContainer.setBounds( 0, 0, width, height);
        info.setText(name);
        info.setFont(new Font("label", 0, height/5));
        info.setVerticalAlignment(0);
        info.setHorizontalAlignment(0);
        imageContainer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        info.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageContainer.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {super.mouseClicked(e);
            try {
                MediaPlayer.open_playlist(name);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }}});
        info.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) {super.mouseClicked(e);
            try {
                MediaPlayer.open_playlist(name);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }}});
        layeredPane.setPreferredSize(new Dimension(300, 300));
        layeredPane.add(imageContainer, new Integer(50));
        layeredPane.add(info, new Integer(100));
        this.add(BorderLayout.NORTH, layeredPane);
        ImageIcon img = new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\photos\\delete.png").getImage().getScaledInstance(width/7, width/7, Image.SCALE_DEFAULT));
        JButton delete = new JButton(img);
        delete.setBackground(Color.red);
        Dimension d = new Dimension(width/7, height);
        delete.setPreferredSize(d);
        delete.setMaximumSize(d);
        delete.addActionListener(new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {MediaPlayer.delete_playlist(name);}});
        delete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        delete.setFocusPainted(false);
        this.add(BorderLayout.SOUTH, delete);
        // CHANGED CODE
        // Manually set layout the components.

        d = new Dimension(width_all, height_all);
        this.setPreferredSize(d);
        this.setMinimumSize(d);
        this.setMaximumSize(d);

    }
}