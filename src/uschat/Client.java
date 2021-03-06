package uschat;


import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class Client {

    private StartWindow startWindow;
    private JFrame textFrame;
    private JFrame infFrame;
    private Font generalFont;
    private JTextField textField;
    private JLabel nameLabel;
    private JButton sendButton;
    private JButton createGroupButton;
    private JButton endButton;
    private BackgroundPanel westPanel;
    private JPanel southPanel;
    private JPanel infPanel;
    private JPanel infNorthPanel;
    private JPanel TextNorthRightPanel;
    private JPanel InfNorthRightPanel;
    private JPanel TextNorthLeftPanel;
    private JPanel InfNorthLeftPanel;
    private JPanel TextNorthTopPanel;
    private JPanel InfNorthTopPanel;
    private JScrollPane userListScroll;
    private JScrollPane friendListScroll;
    private JScrollPane groupListScroll;
    private JTabbedPane ListPanel;
    private JTabbedPane leftPanel;
    private JPopupMenu addMenu;
    private JPopupMenu friendMenu;
    private JPopupMenu groupMenu;
    private JMenuItem addMenuItem;
    private JMenuItem tempStartMenuItem;
    private JMenuItem startMenuItem;
    private JMenuItem endMenuItem;
    private JMenuItem groupStartMenuItem;
    private JMenuItem groupInviteMenuItem;
    private JMenuItem checkGroupMenuItem;
    private JMenuItem quitGroupMenuItem;
    private JList<String> userList;
    private DefaultListModel<String> listModel;
    private JList<String> friendList;
    private DefaultListModel<String> friendListModel;
    private JList<String> groupList;
    private DefaultListModel<String> groupListModel;
    private ArrayList<JTextArea> textAreas;
    private ArrayList<String> Accounts;
    private JButton TextExitButton;
    private JButton TextMinButton;
    private JButton InfExitButton;
    private JButton InfMinButton;
    private ImageIcon close;
    private ImageIcon close_selected;
    private ImageIcon min;
    private ImageIcon min_selected;
    private boolean InfIsDragging;
    private boolean TextIsDragging;
    private int Textxx, Textyy, Infxx, Infyy;

    private User user;

    private boolean isConnected = false;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private MessageThread messageThread;    // ???????????????????????????
    private HashMap<String, Group> groups;  //
    private HashMap<String, User> onLineUsers = new HashMap<String, User>();// ??????????????????

    // ????????????
    public Client() {
        generalFont = new Font("????????????", Font.PLAIN, 20);
        startWindow = new StartWindow();
        textFrame = new JFrame("uschat");
        infFrame = new JFrame("uschat");
        try { // ??????Windows???????????????
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        UIManager.put("Button.font", generalFont);
        UIManager.put("TextField.font", generalFont);
        UIManager.put("List.font", new Font("????????????", Font.BOLD, 20));
        UIManager.put("List.foreground", Color.white);
        UIManager.put("List.background", new Color(0, 0, 0, 0));
        UIManager.put("List.selectionForeground", Color.BLACK);
        UIManager.put("List.selectionBackground", new Color(255, 255, 255, 155));
        UIManager.put("TextArea.font", new Font("????????????", Font.PLAIN, 22));
        UIManager.put("Label.font", generalFont);
        UIManager.put("MenuItem.font", generalFont);
        UIManager.put("TabbedPane.contentOpaque", false);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        close = new ImageIcon("img/close.png");
        close.setImage(close.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH));
        close_selected = new ImageIcon("img/close_selected.png");
        close_selected.setImage(close_selected.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH));
        min = new ImageIcon("img/min.png");
        min.setImage(min.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        min_selected = new ImageIcon("img/min_selected.png");
        min_selected.setImage(min_selected.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

        TextNorthLeftPanel = new JPanel(new FlowLayout());
        TextNorthLeftPanel.add(new JLabel("USCHAT") {
            {
                this.setFont(new Font("????????????", Font.BOLD, 24));
            }
        });
        TextNorthLeftPanel.setOpaque(false);
        InfNorthLeftPanel = new JPanel(new FlowLayout());
        InfNorthLeftPanel.add(new JLabel("USCHAT") {
            {
                this.setFont(new Font("????????????", Font.BOLD, 24));
            }
        });
        InfNorthLeftPanel.setOpaque(false);
        FlowLayout layout = new FlowLayout(FlowLayout.RIGHT);
        layout.setHgap(20);
        TextNorthRightPanel = new JPanel(layout);
        TextMinButton = new JButton();
        TextMinButton.setOpaque(false);
        TextMinButton.setFocusable(false);
        TextMinButton.setIcon(min);
        TextMinButton.setPreferredSize(new Dimension(30, 30));
        TextMinButton.setContentAreaFilled(false);
        TextExitButton = new JButton();
        TextExitButton.setOpaque(false);
        TextExitButton.setFocusable(false);
        TextExitButton.setContentAreaFilled(false);
        TextExitButton.setIcon(close);
        TextExitButton.setPreferredSize(new Dimension(30, 30));
        TextNorthRightPanel.add(TextMinButton);
        TextNorthRightPanel.add(TextExitButton);
        TextNorthRightPanel.setOpaque(false);
        InfNorthRightPanel = new JPanel(layout);
        InfMinButton = new JButton();
        InfMinButton.setOpaque(false);
        InfMinButton.setFocusable(false);
        InfMinButton.setIcon(min);
        InfMinButton.setPreferredSize(new Dimension(30, 30));
        InfMinButton.setContentAreaFilled(false);
        InfExitButton = new JButton();
        InfExitButton.setOpaque(false);
        InfExitButton.setFocusable(false);
        InfExitButton.setContentAreaFilled(false);
        InfExitButton.setIcon(close);
        InfExitButton.setPreferredSize(new Dimension(30, 30));
        InfNorthRightPanel.add(InfMinButton);
        InfNorthRightPanel.add(InfExitButton);
        InfNorthRightPanel.setOpaque(false);
        TextNorthTopPanel = new JPanel(new BorderLayout());
        TextNorthTopPanel.add(TextNorthRightPanel, "East");
        TextNorthTopPanel.add(TextNorthLeftPanel, "West");
        TextNorthTopPanel.setOpaque(false);
        InfNorthTopPanel = new JPanel(new BorderLayout());
        InfNorthTopPanel.add(InfNorthRightPanel, "East");
        InfNorthTopPanel.add(InfNorthLeftPanel, "West");
        InfNorthTopPanel.setOpaque(false);

        textAreas = new ArrayList<>();
        Accounts = new ArrayList<>();
        groups = new HashMap<>();
        textField = new JTextField();
        sendButton = new JButton("??????");
        sendButton.setOpaque(false);
        listModel = new DefaultListModel<>();
        friendListModel = new DefaultListModel<>();
        groupListModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        friendList = new JList<>(friendListModel);
        groupList = new JList<>(groupListModel);

        ListPanel = new JTabbedPane();
        ListPanel.setFont(generalFont);
        ListPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        ListPanel.setOpaque(false);
        userList.setOpaque(false);
        friendList.setOpaque(false);
        groupList.setOpaque(false);

        userListScroll = new JScrollPane(userList);
        userListScroll.setOpaque(false);
        userListScroll.getViewport().setOpaque(false);
        friendListScroll = new JScrollPane(friendList);
        friendListScroll.setOpaque(false);
        friendListScroll.getViewport().setOpaque(false);
        groupListScroll = new JScrollPane(groupList);
        groupListScroll.setOpaque(false);
        groupListScroll.getViewport().setOpaque(false);
        ListPanel.addTab(" ???????????? ", userListScroll);
        ListPanel.addTab(" ???????????? ", friendListScroll);
        ListPanel.addTab(" ???????????? ", groupListScroll);

        leftPanel = new JTabbedPane();
        westPanel = new BackgroundPanel(Toolkit.getDefaultToolkit().createImage("img/text_background.jpg"));
        westPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(new LineBorder(new Color(0,0,0,0),2));
        westPanel.add(TextNorthTopPanel, "North");
        westPanel.add(leftPanel, "Center");
        southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(new TitledBorder(new LineBorder(new Color(0,0,0,0)), "?????????", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.TOP, generalFont));
        southPanel.setOpaque(false);
        southPanel.add(textField, "Center");
        southPanel.add(sendButton, "East");
        westPanel.add(southPanel, "South");
        westPanel.setBorder(new LineBorder(Color.BLACK, 1));

        createGroupButton = new JButton("????????????");
        endButton = new JButton("????????????");
        infNorthPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new BorderLayout());
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel butPanel = new JPanel(new FlowLayout());
        nameLabel = new JLabel(" Hello, User");
        nameLabel.setFont(new Font("????????????", Font.BOLD, 28));
        nameLabel.setForeground(Color.white);
        namePanel.add(nameLabel);
        namePanel.setOpaque(false);
        ImageIcon createGroupIcon = new ImageIcon("img/createGroup.png");
        createGroupIcon.setImage(createGroupIcon.getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH));
        createGroupButton.setOpaque(false);
        createGroupButton.setFocusable(false);
        createGroupButton.setIcon(createGroupIcon);
        ImageIcon reloginIcon = new ImageIcon("img/relogin.png");
        reloginIcon.setImage(reloginIcon.getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH));
        endButton.setOpaque(false);
        endButton.setFocusable(false);
        endButton.setIcon(reloginIcon);
        butPanel.add(createGroupButton);
        butPanel.add(endButton);
        butPanel.setOpaque(false);
        panel.setOpaque(false);
        panel.add(namePanel, "Center");
        panel.add(InfNorthTopPanel, "North");
        infNorthPanel.add(panel, "North");
        infNorthPanel.add(butPanel, "Center");
        infNorthPanel.setOpaque(false);

        infPanel = new BackgroundPanel(Toolkit.getDefaultToolkit().createImage("img/inf_background.jpg"));
        infPanel.setLayout(new BorderLayout());
        infPanel.add(ListPanel, "Center");
        infPanel.add(infNorthPanel, "North");
        infPanel.setOpaque(false);
        infPanel.setBorder(new LineBorder(Color.BLACK, 1));

        addMenu = new JPopupMenu();
        friendMenu = new JPopupMenu();
        groupMenu = new JPopupMenu();
        addMenuItem = new JMenuItem("????????????");
        tempStartMenuItem = new JMenuItem("????????????");
        startMenuItem = new JMenuItem("????????????");
        endMenuItem = new JMenuItem("????????????");
        groupStartMenuItem = new JMenuItem("????????????");
        checkGroupMenuItem = new JMenuItem("???????????????");
        groupInviteMenuItem = new JMenuItem("????????????");
        quitGroupMenuItem = new JMenuItem("????????????");
        addMenu.add(tempStartMenuItem);
        addMenu.add(addMenuItem);
        friendMenu.add(startMenuItem);
        friendMenu.add(endMenuItem);
        groupMenu.add(groupStartMenuItem);
        groupMenu.add(checkGroupMenuItem);
        groupMenu.add(groupInviteMenuItem);
        groupMenu.add(quitGroupMenuItem);

        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setFixedCellHeight(25);
        userList.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.isPopupTrigger() && e.getY() < userList.getFixedCellHeight() * (userList.getSelectedIndex() + 1)
                        && e.getY() > userList.getFixedCellHeight() * userList.getSelectedIndex())
                    addMenu.show(userList, e.getX(), e.getY());
            }
        });
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setFixedCellHeight(25);
        friendList.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.isPopupTrigger() && e.getY() < friendList.getFixedCellHeight() * (friendList.getSelectedIndex() + 1)
                        && e.getY() > friendList.getFixedCellHeight() * friendList.getSelectedIndex()) {
                    if (listModel.contains(friendList.getSelectedValue())) {
                        startMenuItem.setEnabled(true);
                        endMenuItem.setEnabled(true);
                    } else {
                        startMenuItem.setEnabled(false);
                        endMenuItem.setEnabled(false);
                    }
                    friendMenu.show(friendList, e.getX(), e.getY());
                }

            }
        });
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupList.setFixedCellHeight(25);
        groupList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.isPopupTrigger() && e.getY() < groupList.getFixedCellHeight() * (groupList.getSelectedIndex() + 1)
                        && e.getY() > groupList.getFixedCellHeight() * groupList.getSelectedIndex()) {
                    if (groupList.getSelectedIndex() == 0) {
                        quitGroupMenuItem.setEnabled(false);
                        checkGroupMenuItem.setEnabled(false);
                        groupInviteMenuItem.setEnabled(false);
                    } else {
                        quitGroupMenuItem.setEnabled(true);
                        checkGroupMenuItem.setEnabled(true);
                        groupInviteMenuItem.setEnabled(true);
                    }
                    groupMenu.show(groupList, e.getX(), e.getY());
                }
            }
        });

        textFrame.setLayout(new BorderLayout());
        textFrame.add(westPanel, "Center");
        textFrame.setSize(800, 700);
        textFrame.setUndecorated(true);
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        textFrame.setLocation((screen_width - textFrame.getWidth()) / 2, (screen_height - textFrame.getHeight()) / 2);
        infFrame.setLayout(new BorderLayout());
        infFrame.add(infPanel, "Center");
        infFrame.setUndecorated(true);
        infFrame.setSize(315, 750);
        infFrame.setLocation(screen_width - infFrame.getWidth() - 200, (screen_height - infFrame.getHeight()) / 2);
        infFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initializeTab();

        infFrame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                InfIsDragging = true;
                Infxx = e.getX();
                Infyy = e.getY();
            }

            public void mouseReleased(MouseEvent e) {
                InfIsDragging = false;
            }
        });
        infFrame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (InfIsDragging) {
                    int left = infFrame.getLocation().x;
                    int top = infFrame.getLocation().y;
                    infFrame.setLocation(left + e.getX() - Infxx, top + e.getY() - Infyy);

                }
            }
        });

        textFrame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                TextIsDragging = true;
                Textxx = e.getX();
                Textyy = e.getY();
            }

            public void mouseReleased(MouseEvent e) {
                TextIsDragging = false;
            }
        });
        textFrame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (TextIsDragging) {
                    int left = textFrame.getLocation().x;
                    int top = textFrame.getLocation().y;
                    textFrame.setLocation(left + e.getX() - Textxx, top + e.getY() - Textyy);
                }
            }
        });

        TextMinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.setExtendedState(JFrame.ICONIFIED);
            }
        });

        TextMinButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                TextMinButton.setIcon(min_selected);
            }
        });

        TextMinButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                TextMinButton.setIcon(min);
            }
        });

        TextExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.setVisible(false);
            }
        });

        TextExitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                TextExitButton.setIcon(close_selected);
            }
        });

        TextExitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                TextExitButton.setIcon(close);
            }
        });

        InfMinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infFrame.setExtendedState(JFrame.ICONIFIED);
            }
        });

        InfMinButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                InfMinButton.setIcon(min_selected);
            }
        });

        InfMinButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                InfMinButton.setIcon(min);
            }
        });

        InfExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isConnected) {
                    closeConnection();// ????????????
                }
                System.exit(0);// ????????????
            }
        });

        InfExitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                InfExitButton.setIcon(close_selected);
            }
        });

        InfExitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                InfExitButton.setIcon(close);
            }
        });

        // ?????????
        addMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String account = userList.getSelectedValue();
                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                if (!friendListModel.contains(userList.getSelectedValue())) {
                    sendMessage("COMMAND@ADDFRIEND@" + user.getAccount() + "@" + account);
                } else {
                    JOptionPane.showMessageDialog(infFrame, "???????????? " + account + " ?????????",
                            "??????", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        // ??????????????????
        tempStartMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String account = userList.getSelectedValue();
                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                if (!Accounts.contains(account)) {
                    String nickName = onLineUsers.get(account).getNickName();
                    createTab(nickName, account);
                    leftPanel.setSelectedIndex(textAreas.size() - 1);
                } else {
                    leftPanel.setSelectedIndex(Accounts.indexOf(account));
                }
                if (!textFrame.isVisible())
                    textFrame.setVisible(true);
            }
        });

        // ????????????
        endMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String account = friendList.getSelectedValue();
                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                sendMessage("COMMAND@ENDFRIEND@" + user.getAccount() + "@" + account);
                if (Accounts.contains(account)) {
                    int index = Accounts.indexOf(account);
                    textAreas.remove(index);
                    Accounts.remove(index);
                    leftPanel.removeTabAt(index);
                }
                friendListModel.removeElementAt(friendList.getSelectedIndex());
            }
        });

        // ??????????????????
        startMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String account = friendList.getSelectedValue();
                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                if (!Accounts.contains(account)) {
                    String nickName = onLineUsers.get(account).getNickName();
                    createTab(nickName, account);
                    leftPanel.setSelectedIndex(textAreas.size() - 1);
                } else {
                    leftPanel.setSelectedIndex(Accounts.indexOf(account));
                }
                if (!textFrame.isVisible())
                    textFrame.setVisible(true);
            }
        });

        // ????????????
        groupStartMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String account = groupList.getSelectedValue();
                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                if (!Accounts.contains(account)) {
                    String name = groups.get(account).getName();
                    createTab(name, account);
                    leftPanel.setSelectedIndex(textAreas.size() - 1);
                } else {
                    leftPanel.setSelectedIndex(Accounts.indexOf(account));
                }
                if (!textFrame.isVisible())
                    textFrame.setVisible(true);
            }
        });

        // ???????????????
        checkGroupMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String account = groupList.getSelectedValue();
                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                String memberAccount;
                String memberName;
                String allMember = "";
                for (int i = 0; i < groups.get(account).getMembers().size(); i++) {
                    memberAccount = groups.get(account).getMembers().get(i);
                    if (memberAccount.equals(user.getAccount())) {
                        memberName = user.getNickName();
                    } else {
                        memberName = onLineUsers.get(memberAccount).getNickName();
                    }
                    allMember += memberName + " (" + memberAccount + ")\n";
                }
                JOptionPane.showMessageDialog(infFrame, allMember, "?????????", JOptionPane.PLAIN_MESSAGE);
            }
        });

        // ??????????????????
        groupInviteMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("??????????????????");
                JPanel mainPanel = new JPanel(new BorderLayout());
                JPanel itemPanel = new JPanel(new GridLayout(0, 1));
                JScrollPane scrollPane = new JScrollPane(itemPanel);
                Vector<JCheckBox> checkBoxes = new Vector<>();
                JPanel buttonPanel = new JPanel(new FlowLayout());
                JButton confirmButton = new JButton("??????");
                JButton cancelButton = new JButton("??????");
                String groupAccount = groupList.getSelectedValue();
                groupAccount = groupAccount.substring(groupAccount.indexOf("(") + 1, groupAccount.indexOf(")"));
                String groupName = groups.get(groupAccount).getName();
                Vector<String> members = groups.get(groupAccount).getMembers();
                for (int i = 0; i < friendListModel.size(); i++) {
                    String friend = friendListModel.get(i);
                    String friendAccount = friend.substring(friend.indexOf("(") + 1, friend.indexOf(")"));
                    JCheckBox checkBox = new JCheckBox(friend);
                    checkBox.setFont(generalFont);
                    if (members.contains(friendAccount)) {
                        checkBox.setEnabled(false);
                    }
                    JPanel panel1 = new JPanel(new FlowLayout());
                    panel1.add(checkBox);
                    itemPanel.add(panel1);
                    checkBoxes.add(checkBox);
                }
                buttonPanel.add(confirmButton);
                buttonPanel.add(cancelButton);
                mainPanel.add(scrollPane, "Center");
                mainPanel.add(buttonPanel, "South");
                frame.add(mainPanel);
                frame.setSize(200, 400);
                frame.setLocationRelativeTo(infFrame);
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
                String finalGroupAccount = groupAccount;
                confirmButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (JCheckBox checkBox : checkBoxes) {
                            if (checkBox.isSelected()) {
                                String account = checkBox.getText();
                                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                                sendMessage("COMMAND@JOINGROUP@" + user.getAccount() + "@" + account + "@" +
                                        finalGroupAccount + "@" + groupName);
                            }
                        }
                        frame.dispose();
                    }
                });
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                    }
                });
            }
        });

        // ????????????
        quitGroupMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupAccount = groupList.getSelectedValue();
                groupAccount = groupAccount.substring(groupAccount.indexOf("(") + 1, groupAccount.indexOf(")"));
                sendMessage("COMMAND@QUITGROUP@" + user.getAccount() + "@" + groupAccount);
                if (Accounts.contains(groupAccount)) {
                    int index = Accounts.indexOf(groupAccount);
                    textAreas.remove(index);
                    Accounts.remove(index);
                    leftPanel.removeTabAt(index);
                }
                groups.remove(groupAccount);
                groupListModel.removeElementAt(groupList.getSelectedIndex());
            }
        });
        // ?????????????????????????????????????????????
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                send();
            }
        });

        // ???????????????????????????
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });

        // ?????????????????????????????????
        createGroupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String groupName;
                while (true) {
                    groupName = JOptionPane.showInputDialog(infFrame, "????????????????????????", "????????????"
                            , JOptionPane.PLAIN_MESSAGE);
                    if (groupName == null) {
                        return;
                    } else if (groupName.equals("")) {
                        JOptionPane.showMessageDialog(infFrame, "???????????????",
                                "??????", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        break;
                    }
                }
                sendMessage("COMMAND@CREATEGROUP@" + user.getAccount() + "@" + groupName);
            }
        });

        // ???????????????????????????
        endButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isConnected) {
                    JOptionPane.showMessageDialog(infFrame, "??????????????????????????????????????????!", "??????", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                try {
                    int isRelogin = JOptionPane.showConfirmDialog(infFrame, "?????????????????????", "????????????", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (isRelogin == 0) {
                        closeConnection();// ????????????
                        startWindow.accountFiled.setText(null);
                        startWindow.passwordField.setText(null);
                        startWindow.startFrame.setVisible(true);
                        FramePosition.toCenter(startWindow.startFrame);
                        infFrame.setVisible(false);
                        textFrame.setVisible(false);
                    }
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(infFrame, exc.getMessage(), "??????", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        infFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (isConnected) {
                    closeConnection();// ????????????
                }
                System.exit(0);// ????????????
            }
        });
    }

    public void initializeTab() {
        groupListModel.add(0, "???????????? (0)");
        groups.put("0", new Group("????????????", "0"));
        String account = "0";
        String name = groups.get(account).getName();
        textAreas.add(new JTextArea());
        JTab tab = new JTab(name, 0);
        textAreas.get(0).setEditable(false);
        textAreas.get(0).setOpaque(false);
        textAreas.get(0).setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textAreas.get(0));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        Accounts.add(account);
        leftPanel.addTab(name, scrollPane);
        leftPanel.setTabComponentAt(0, tab);
        leftPanel.setSelectedIndex(textAreas.size() - 1);
    }

    // ????????????
    public void send() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(textFrame, "????????????????????????????????????????????????", "??????", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        String message = textField.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(textFrame, "?????????????????????", "??????", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        if (Accounts.get(leftPanel.getSelectedIndex()).equals("0")) {
            sendMessage("MESSAGE@" + user.getNickName() + "@ALL@" + message);
        } else if (onLineUsers.containsKey(Accounts.get(leftPanel.getSelectedIndex()))) {
            sendMessage("MESSAGE@" + user.getAccount() + "@" + Accounts.get(leftPanel.getSelectedIndex()) + "@" + message);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(new Date());
            textAreas.get(leftPanel.getSelectedIndex()).append("???" + "      " + time + "\r\n"  + message + " \r\n");
        } else {
            sendMessage("MESSAGE@" + user.getNickName() + "@" + Accounts.get(leftPanel.getSelectedIndex()) + "@" + message);
        }
        textField.setText(null);
    }

    // ??????????????????
    public void createTab(String name, String account) {
        textAreas.add(new JTextArea());
        JTab tab = new JTab(name, textAreas.size() - 1);
        textAreas.get(textAreas.size() - 1).setEditable(false);
        textAreas.get(textAreas.size() - 1).setOpaque(false);
        Accounts.add(account);
        JScrollPane scrollPane = new JScrollPane(textAreas.get(textAreas.size() - 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        leftPanel.addTab(name, scrollPane);
        leftPanel.setTabComponentAt(textAreas.size() - 1, tab);
        JPopupMenu tabMenu = new JPopupMenu();
        JMenuItem closeMenuItem = new JMenuItem("????????????");
        tabMenu.add(closeMenuItem);
        if (!textFrame.isVisible())
            textFrame.setVisible(true);
        tab.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    tabMenu.show(tab, e.getX(), e.getY());
                } else {
                    leftPanel.setSelectedIndex(tab.getIndex());
                }
            }
        });
        closeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = tab.getIndex();
                Accounts.remove(index);
                textAreas.remove(index);
                leftPanel.removeTabAt(index);
                if (leftPanel.getSelectedIndex() == index) {
                    leftPanel.setSelectedIndex(0);
                }
            }
        });
    }

    // ????????????
    public void sendMessage(String message) {
        writer.println(message);
        writer.flush();
    }

    // ???????????????????????????
    public synchronized void closeConnection() {
        try {
            sendMessage("COMMAND@CLOSE");// ????????????????????????????????????
            // ????????????
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null) {
                socket.close();
            }
            isConnected = false;
            listModel.removeAllElements();
        } catch (IOException e1) {
            e1.printStackTrace();
            isConnected = true;
        }
    }

    // ?????????????????????


    // ????????????
    class StartWindow {
        private JFrame startFrame;
        private BackgroundPanel mainPanel;
        private JPanel northLeftPanel;
        private JPanel northRightPanel;
        private JPanel northPanel;
        private JPanel accountPanel;
        private JPanel passwordPanel;
        private JPanel confirmPasswordPanel;
        private JPanel centerPanel;
        private JPanel southPanel;
        private JPanel buttonPanel;
        private JTextField accountFiled;
        private JPasswordField passwordField;
        private JPasswordField confirmPasswordField;
        private JButton registerButton;
        private JButton loginButton;
        private JButton confirmRegisterButton;
        private JButton cancelRegisterButton;
        private JButton minButton;
        private JButton exitButton;
        private ImageIcon close;
        private ImageIcon min;
        private ImageIcon close_selected;
        private ImageIcon min_selected;
        private boolean isDraging;
        private int xx, yy;

        public StartWindow() {
            startFrame = new JFrame("uschat");
            startFrame.setIconImage(null);
            try { // ??????Windows???????????????
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            user = new User();
            UIManager.put("Button.font", generalFont);
            UIManager.put("TextField.font", new Font("????????????", Font.PLAIN, 25));
            UIManager.put("Label.font", new Font("????????????", Font.BOLD, 25));

            close = new ImageIcon("img/close.png");
            close.setImage(close.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH));
            close_selected = new ImageIcon("img/close_selected.png");
            close_selected.setImage(close_selected.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH));
            min = new ImageIcon("img/min.png");
            min.setImage(min.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            min_selected = new ImageIcon("img/min_selected.png");
            min_selected.setImage(min_selected.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

            northLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            ImageIcon icon = new ImageIcon("img/uschat.png");
            icon.setImage(icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
            northLeftPanel.add(new JLabel(icon));
            northLeftPanel.add(new JLabel("USCHAT") {
                {
                    this.setForeground(Color.white);
                    this.setFont(new Font("????????????", Font.BOLD, 30));
                }
            });
            northLeftPanel.setOpaque(false);
            FlowLayout layout = new FlowLayout(FlowLayout.RIGHT);
            layout.setHgap(20);
            northRightPanel = new JPanel(layout);
            minButton = new JButton();
            minButton.setOpaque(false);
            minButton.setFocusable(false);
            minButton.setIcon(min);
            minButton.setPreferredSize(new Dimension(30, 30));
            minButton.setContentAreaFilled(false);
            exitButton = new JButton();
            exitButton.setOpaque(false);
            exitButton.setFocusable(false);
            exitButton.setContentAreaFilled(false);
            exitButton.setIcon(close);
            exitButton.setPreferredSize(new Dimension(30, 30));
            northRightPanel.add(minButton);
            northRightPanel.add(exitButton);
            northRightPanel.setOpaque(false);
            northPanel = new JPanel(new GridLayout(1, 2));
            northPanel.add(northLeftPanel);
            northPanel.add(northRightPanel);
            northPanel.setOpaque(false);

            accountFiled = new JTextField();
            accountFiled.setPreferredSize(new Dimension(200, 30));
            passwordField = new JPasswordField();
            passwordField.setPreferredSize(new Dimension(200, 30));
            confirmPasswordField = new JPasswordField();
            confirmPasswordField.setPreferredSize(new Dimension(200, 30));

            accountPanel = new JPanel(new FlowLayout());
            accountPanel.add(new JLabel("?????????") {
                {
                    this.setForeground(Color.white);
                }
            });
            accountPanel.add(accountFiled);
            accountPanel.setOpaque(false);
            passwordPanel = new JPanel(new FlowLayout());
            passwordPanel.add(new JLabel("?????????") {
                {
                    this.setForeground(Color.white);
                }
            });
            passwordPanel.add(passwordField);
            passwordPanel.setOpaque(false);
            confirmPasswordPanel = new JPanel(new FlowLayout());
            confirmPasswordPanel.add(new JLabel("???????????????") {
                {
                    this.setForeground(Color.white);
                }
            });
            confirmPasswordPanel.add(confirmPasswordField);
            confirmPasswordPanel.add(new JLabel("      "));
            confirmPasswordPanel.setOpaque(false);
            centerPanel = new JPanel();
            centerPanel.setLayout(new GridLayout(2, 1));
            centerPanel.add(accountPanel);
            centerPanel.add(passwordPanel);
            centerPanel.setOpaque(false);

            registerButton = new JButton("??????");
            registerButton.setPreferredSize(new Dimension(125, 40));
            registerButton.setOpaque(false);
            confirmRegisterButton = new JButton("????????????");
            confirmRegisterButton.setPreferredSize(new Dimension(150, 40));
            cancelRegisterButton = new JButton("??????");
            cancelRegisterButton.setPreferredSize(new Dimension(125, 40));
            loginButton = new JButton("??????");
            loginButton.setPreferredSize(new Dimension(125, 40));
            loginButton.setOpaque(false);
            southPanel = new JPanel();
            southPanel.setLayout(new BorderLayout());
            buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(registerButton);
            buttonPanel.add(loginButton);
            buttonPanel.setOpaque(false);
            southPanel.add(new JLabel(), "Center");
            southPanel.add(buttonPanel, "South");
            southPanel.setOpaque(false);

            mainPanel = new BackgroundPanel(Toolkit.getDefaultToolkit().createImage("img/background.jpeg"));
            mainPanel.setLayout(new GridLayout(3, 1));
            mainPanel.add(northPanel);
            mainPanel.add(centerPanel);
            mainPanel.add(southPanel);
            mainPanel.setOpaque(false);


            startFrame.setSize(600, 360);
            startFrame.add(mainPanel);
            startFrame.setUndecorated(true);
            startFrame.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    isDraging = true;
                    xx = e.getX();
                    yy = e.getY();
                }

                public void mouseReleased(MouseEvent e) {
                    isDraging = false;
                }
            });
            startFrame.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (isDraging) {
                        int left = startFrame.getLocation().x;
                        int top = startFrame.getLocation().y;
                        startFrame.setLocation(left + e.getX() - xx, top + e.getY() - yy);

                    }
                }
            });

            startFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            FramePosition.toCenter(startFrame);
            startFrame.setVisible(true);

            minButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startFrame.setExtendedState(JFrame.ICONIFIED);
                }
            });

            minButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    minButton.setIcon(min_selected);
                }
            });

            minButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    minButton.setIcon(min);
                }
            });

            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            exitButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    exitButton.setIcon(close_selected);
                }
            });

            exitButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    exitButton.setIcon(close);
                }
            });

            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buttonPanel.removeAll();
                    buttonPanel.add(confirmRegisterButton);
                    buttonPanel.add(cancelRegisterButton);
                    buttonPanel.repaint();
                    buttonPanel.revalidate();
                    accountFiled.setText(null);
                    passwordField.setText(null);
                    confirmPasswordField.setText(null);
                    centerPanel.setLayout(new GridLayout(3, 1));
                    centerPanel.removeAll();
                    centerPanel.add(accountPanel);
                    centerPanel.add(passwordPanel);
                    centerPanel.add(confirmPasswordPanel);
                    centerPanel.repaint();
                    centerPanel.revalidate();
                }
            });

            confirmRegisterButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String account;
                    String password;
                    String confirmPassword;
                    try {
                        account = accountFiled.getText();
                        if (account == null || account.equals("")) {
                            throw new Exception("?????????????????????");
                        }
                        password = new String(passwordField.getPassword());
                        if (password.isEmpty()) {
                            throw new Exception("?????????????????????");
                        }
                        confirmPassword = new String(confirmPasswordField.getPassword());
                        if (confirmPassword.isEmpty()) {
                            throw new Exception("??????????????????");
                        }
                        if (!confirmPassword.equals(password)) {
                            throw new Exception("??????????????????");
                        }
                        if (!isConnected) {
                            socket = new Socket("127.0.0.1", 6666);// ???????????????????????????IP????????????
                            writer = new PrintWriter(socket.getOutputStream());
                            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            isConnected = true;
                        }
                        // ?????????????????????????????????
                        user.setAccount(account);
                        user.setPassword(password);
                        sendMessage("REGISTER@" + user.getAccount() + "@" + user.getPassword());
                        String message = reader.readLine();
                        if (message.equals("SUCCESS")) {
                            JOptionPane.showMessageDialog(startFrame, "???????????????", "??????", JOptionPane.PLAIN_MESSAGE);
                            toMainPanel();
                        } else {
                            throw new Exception("?????????????????????");
                        }

                    } catch (IOException exc) {
                        JOptionPane.showMessageDialog(startFrame, "?????????????????????", "??????", JOptionPane.PLAIN_MESSAGE);
                    } catch (Exception exc2) {
                        JOptionPane.showMessageDialog(startFrame, exc2.getMessage(), "??????", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            });

            cancelRegisterButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toMainPanel();
                }
            });

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String account;
                    String password;
                    String nickName;
                    try {
                        account = accountFiled.getText();
                        if (account == null || account.equals("")) {
                            throw new Exception("?????????????????????");
                        }
                        password = new String(passwordField.getPassword());
                        if (password.isEmpty()) {
                            throw new Exception("?????????????????????");
                        }
                        if (!isConnected) {
                            socket = new Socket("127.0.0.1", 6666);// ???????????????????????????IP????????????
                            writer = new PrintWriter(socket.getOutputStream());
                            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        }
                        // ?????????????????????????????????(????????????IP??????)
                        user.setAccount(account);
                        user.setPassword(password);

                        sendMessage("LOGIN@" + user.getAccount() + "@" + user.getPassword());

                        String message = reader.readLine();
                        switch (message) {
                            case "MAX": // ??????????????????
                                JOptionPane.showMessageDialog(startFrame, "???????????????????????????", "??????", JOptionPane.PLAIN_MESSAGE);
                                // ??????????????????
                                listModel.removeAllElements();
                                // ?????????????????????????????????
                                if (reader != null) {
                                    reader.close();
                                }
                                if (writer != null) {
                                    writer.close();
                                }
                                if (socket != null) {
                                    socket.close();
                                }
                                isConnected = false;// ?????????????????????

                                break;
                            case "SUCCESS":
                                while (true) {
                                    nickName = JOptionPane.showInputDialog(startFrame, "?????????????????????????????????", "????????????"
                                            , JOptionPane.PLAIN_MESSAGE);
                                    if (nickName == null) {
                                        return;
                                    } else if (nickName.equals("")) {
                                        JOptionPane.showMessageDialog(startFrame, "???????????????",
                                                "??????", JOptionPane.PLAIN_MESSAGE);
                                    } else
                                        break;
                                }
                                user.setNickName(nickName);
                                sendMessage(user.getNickName());
                                // ???????????????????????????
                                messageThread = new MessageThread(reader, textAreas);
                                messageThread.start();
                                isConnected = true;// ??????????????????

                                nameLabel.setText(" Hello, " + user.getNickName());
                                startFrame.setVisible(false);
                                infFrame.setVisible(true);
                                textFrame.setVisible(true);
                                break;
                            case "DUPLICATED":
                                throw new Exception("????????????");
                            default:
                                throw new Exception("???????????????????????????????????????");
                        }
                    } catch (IOException exc) {
                        JOptionPane.showMessageDialog(startFrame, "?????????????????????", "??????", JOptionPane.PLAIN_MESSAGE);
                    } catch (Exception exc2) {
                        JOptionPane.showMessageDialog(startFrame, exc2.getMessage(), "??????", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            });
        }

        public void toMainPanel() {
            buttonPanel.removeAll();
            buttonPanel.add(registerButton);
            buttonPanel.add(loginButton);
            buttonPanel.repaint();
            buttonPanel.revalidate();
            accountFiled.setText(null);
            passwordField.setText(null);
            confirmPasswordField.setText(null);
            centerPanel.setLayout(new GridLayout(2, 1));
            centerPanel.removeAll();
            centerPanel.add(accountPanel);
            centerPanel.add(passwordPanel);
            centerPanel.repaint();
            centerPanel.revalidate();
        }

    }

    // ???????????????????????????
    class MessageThread extends Thread {
        private BufferedReader reader;
        private ArrayList<JTextArea> textAreas;

        // ?????????????????????????????????
        public MessageThread(BufferedReader reader, ArrayList<JTextArea> textAreas) {
            this.reader = reader;
            this.textAreas = textAreas;
        }

        // ?????????????????????
        public synchronized void closeCon() throws Exception {
            // ??????????????????
            listModel.removeAllElements();
            // ?????????????????????????????????
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null) {
                socket.close();
            }
            isConnected = false;// ?????????????????????
        }

        public void run() {
            String message;
            while (isConnected) {
                try {
                    message = reader.readLine();
                    if (message == null)
                        continue;
                    System.out.println(message);
                    StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
                    String type = stringTokenizer.nextToken();// ??????
                    if (type.equals("COMMAND")) {
                        String command = stringTokenizer.nextToken();
                        switch (command) {
                            case "CLOSE":// ????????????????????????
                                textAreas.get(Accounts.indexOf("0")).append("??????????????????!\r\n");
                                closeCon();// ?????????????????????
                                closeConnection();// ????????????
                                startWindow.accountFiled.setText(null);
                                startWindow.passwordField.setText(null);
                                startWindow.startFrame.setVisible(true);
                                FramePosition.toCenter(startWindow.startFrame);
                                infFrame.setVisible(false);
                                textFrame.setVisible(false);
                                JOptionPane.showMessageDialog(infFrame, "??????????????????!", "??????", JOptionPane.PLAIN_MESSAGE);
                                return;// ????????????

                            case "ADD": {// ?????????????????????????????????
                                String userAccount;
                                String userNickName = "";
                                if ((userAccount = stringTokenizer.nextToken()) != null
                                        && (userNickName = stringTokenizer.nextToken()) != null) {
                                    User user = new User(userAccount, userNickName);
                                    textAreas.get(Accounts.indexOf("0")).append(user.getNickName() + " (" + user.getAccount() + ")" + " ????????????\r\n");
                                    onLineUsers.put(userAccount, user);
                                    listModel.addElement(user.getNickName() + " (" + userAccount + ")");
                                }
                                String account;
                                for (int i = 0; i < friendListModel.size(); i++) {
                                    account = friendListModel.get(i);
                                    account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                                    if (account.equals(userAccount)) {
                                        friendListModel.set(i, userNickName + " (" + userAccount + ")");
                                    }
                                }
                                break;
                            }
                            case "DELETE": {// ?????????????????????????????????
                                String userAccount = stringTokenizer.nextToken();
                                User user = onLineUsers.get(userAccount);
                                textAreas.get(Accounts.indexOf("0")).append(user.getNickName() + " (" + user.getAccount() + ")" + " ????????????\r\n");
                                onLineUsers.remove(user.getAccount());
                                listModel.removeElement(user.getNickName() + " (" + userAccount + ")");
                                if (Accounts.contains(userAccount)) {
                                    int index = Accounts.indexOf(userAccount);
                                    textAreas.remove(index);
                                    Accounts.remove(index);
                                    leftPanel.removeTabAt(index);
                                }
                                break;
                            }
                            case "USERLIST": {// ????????????????????????
                                int size = Integer.parseInt(stringTokenizer.nextToken());
                                String userAccount;
                                String username;
                                for (int i = 0; i < size; i++) {
                                    userAccount = stringTokenizer.nextToken();
                                    username = stringTokenizer.nextToken();
                                    User user = new User(userAccount, username);
                                    onLineUsers.put(userAccount, user);
                                    listModel.addElement(username + " (" + userAccount + ")");
                                }
                                break;
                            }
                            case "KICK": {// ????????????
                                closeConnection();// ????????????
                                JOptionPane.showMessageDialog(textFrame, "???????????????????????????", "??????", JOptionPane.PLAIN_MESSAGE);
                                startWindow.accountFiled.setText(null);
                                startWindow.passwordField.setText(null);
                                startWindow.startFrame.setVisible(true);
                                FramePosition.toCenter(startWindow.startFrame);
                                infFrame.setVisible(false);
                                textFrame.setVisible(false);
                                return;// ????????????
                            }
                            case "ADDFRIEND": {
                                String source = stringTokenizer.nextToken();
                                int choice = JOptionPane.showConfirmDialog(infFrame,
                                        onLineUsers.get(source).getNickName() + " (" + source + ") ????????????????????????",
                                        "????????????", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                                if (choice == JOptionPane.YES_OPTION) {
                                    User friend = onLineUsers.get(source);
                                    friendListModel.addElement(friend.getNickName() + " (" + friend.getAccount() + ")");
                                    sendMessage("COMMAND@FRIENDAGREED@" + user.getAccount() + "@" + source);
                                }
                                break;
                            }
                            case "FRIENDAGREED": {
                                String source = stringTokenizer.nextToken();
                                JOptionPane.showMessageDialog(infFrame,
                                        onLineUsers.get(source).getNickName() + " (" + source + ") ???????????????????????????",
                                        "??????", JOptionPane.PLAIN_MESSAGE);
                                User friend = onLineUsers.get(source);
                                friendListModel.addElement(friend.getNickName() + " (" + friend.getAccount() + ")");
                                break;
                            }
                            case "ENDFRIEND": {
                                String source = stringTokenizer.nextToken();
                                JOptionPane.showMessageDialog(infFrame,
                                        onLineUsers.get(source).getNickName() + " (" + source + ") ??????????????????",
                                        "??????", JOptionPane.PLAIN_MESSAGE);
                                if (Accounts.contains(source)) {
                                    int index = Accounts.indexOf(source);
                                    textAreas.remove(index);
                                    Accounts.remove(index);
                                    leftPanel.removeTabAt(index);
                                }
                                User friend = onLineUsers.get(source);
                                friendListModel.removeElement(friend.getNickName() + " (" + friend.getAccount() + ")");
                                break;
                            }
                            case "CREATEGROUP": {
                                String groupAccount = stringTokenizer.nextToken();
                                String groupName = stringTokenizer.nextToken();
                                Group group = new Group(groupName, groupAccount);
                                group.getMembers().add(user.getAccount());
                                groups.put(groupAccount, group);
                                groupListModel.addElement(groupName + " (" + groupAccount + ")");
                                JOptionPane.showMessageDialog(infFrame,
                                        "??? " + groupName + " ???????????????????????????" + groupAccount,
                                        "??????", JOptionPane.PLAIN_MESSAGE);
                                break;
                            }
                            case "QUITGROUP": {
                                String source = stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                JOptionPane.showMessageDialog(infFrame,
                                        onLineUsers.get(source).getNickName() + " (" + source + ") ????????????" +
                                                groups.get(groupAccount).getName() + " (" + groupAccount + ")",
                                        "????????????", JOptionPane.PLAIN_MESSAGE);
                                groups.get(groupAccount).getMembers().remove(source);
                                break;
                            }
                            case "JOINGROUP": {
                                String source = stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                String groupName = stringTokenizer.nextToken();
                                int choice = JOptionPane.showConfirmDialog(infFrame,
                                        onLineUsers.get(source).getNickName() + " (" + source + ") ?????????????????????" +
                                                groupName + " (" + groupAccount + ")",
                                        "????????????", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                                if (choice == JOptionPane.YES_OPTION) {
                                    Group group = new Group(groupName, groupAccount);
                                    groups.put(groupAccount, group);
                                    groupListModel.addElement(groupName + " (" + groupAccount + ")");
                                    sendMessage("COMMAND@JOINAGREED@" + user.getAccount() + "@" + source + "@" + groupAccount);
                                }
                                break;
                            }
                            case "JOINAGREED": {
                                String source = stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                groups.get(groupAccount).getMembers().add(source);
                                JOptionPane.showMessageDialog(infFrame, onLineUsers.get(source).getNickName() +
                                        "??????????????????" + groupAccount, "??????", JOptionPane.PLAIN_MESSAGE);
                                String memberAccount;
                                String allMember = "";
                                for (int i = 0; i < groups.get(groupAccount).getMembers().size(); i++) {
                                    memberAccount = groups.get(groupAccount).getMembers().get(i);
                                    allMember += memberAccount + "#";
                                }
                                sendMessage("COMMAND@UPDATEGROUP@" + user.getAccount() + "@" + source + "@" + groupAccount +
                                        "@" + groups.get(groupAccount).getMembers().size() + "@" + allMember);
                                break;
                            }
                            case "UPDATEGROUP": {
                                stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                int memberSize = Integer.parseInt(stringTokenizer.nextToken());
                                String members = stringTokenizer.nextToken();
                                StringTokenizer allMember = new StringTokenizer(members, "#");
                                String memberAccount;
                                groups.get(groupAccount).getMembers().clear();
                                for (int i = 0; i < memberSize; i++) {
                                    memberAccount = allMember.nextToken();
                                    groups.get(groupAccount).getMembers().add(memberAccount);
                                }
                                break;
                            }
                        }
                    } else if (type.equals("MESSAGE")) {
                        String target = stringTokenizer.nextToken();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = df.format(new Date());
                        switch (target) {
                            case "ALL": {
                                String nickName = stringTokenizer.nextToken();
                                String text = stringTokenizer.nextToken();
                                textAreas.get(Accounts.indexOf("0")).append(nickName + "      " + time + "\r\n"  + text + "\r\n");
                                break;
                            }
                            case "PERSONAL": {
                                String account = stringTokenizer.nextToken();
                                String text = stringTokenizer.nextToken();
                                if (Accounts.contains(account)) {// ???????????????????????????
                                    int index = Accounts.indexOf(account);
                                    textAreas.get(index).append("??????" + "      " + time + "\r\n"  + text + "\r\n");
                                    leftPanel.setSelectedIndex(index);
                                } else {// ??????????????????
                                    String name = onLineUsers.get(account).getNickName();
                                    createTab(name, account);
                                    textAreas.get(textAreas.size() - 1).append("??????" + "      " + time + "\r\n"  + text + "\r\n");
                                    leftPanel.setSelectedIndex(textAreas.size() - 1);
                                }
                                if (!textFrame.isVisible())
                                    textFrame.setVisible(true);
                                break;
                            }
                            case "GROUP": {
                                String nickName = stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                String text = stringTokenizer.nextToken();
                                if (Accounts.contains(groupAccount)) {// ???????????????????????????
                                    int index = Accounts.indexOf(groupAccount);
                                    textAreas.get(index).append(nickName + "      " + time + "\r\n"  + text + "\r\n");
                                    leftPanel.setSelectedIndex(index);
                                } else {// ??????????????????
                                    String name = groups.get(groupAccount).getName();
                                    createTab(name, groupAccount);
                                    textAreas.get(textAreas.size() - 1).append(nickName + "      " + time + "\r\n"  + text + "\r\n");
                                    leftPanel.setSelectedIndex(textAreas.size() - 1);
                                }
                                if (!textFrame.isVisible())
                                    textFrame.setVisible(true);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ?????????,????????????
    public static void main(String[] args) {
        new Client();
    }
}