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
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Server {

    private JFrame frame;
    private JTextArea contentArea;
    private JTextField messageField;
    private JTextField maxField;
    private final Font generalFont;
    private JButton startButton;
    private JButton stopButton;
    private JButton sendButton;
    private JPanel northPanel;
    private JPanel northPanelLeft;
    private JPanel northPanelRight;
    private JPanel southPanel;
    private JPanel rightPanel;
    private JScrollPane rightScroll;
    private JScrollPane leftScroll;
    private JSplitPane centerSplit;
    private JPopupMenu kickMenu;
    private JMenuItem kick;
    private JList<String> userList;
    private DefaultListModel<String> listModel;
    private HashMap<String, User> users;
    private HashMap<String, User> onlineUsers;
    private HashMap<String, Group> groups;


    private ServerSocket serverSocket;
    private ServerThread serverThread;
    private ArrayList<ClientThread> clients;

    private boolean isStart = false;

    // 主方法,程序执行入口
    public static void main(String[] args) {
        new Server();
    }

    // 执行消息发送
    public void send() {
        if (!isStart) {
            JOptionPane.showMessageDialog(frame, "服务器还未启动,不能发送消息！", "错误", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        if (clients.size() == 0) {
            JOptionPane.showMessageDialog(frame, "没有用户在线,不能发送消息！", "错误", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        String message = messageField.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "消息不能为空！", "错误", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        sendServerMessage(message);// 群发服务器消息
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(new Date());
        contentArea.append("服务器" + "      " + time + "\r\n"  + messageField.getText() + "\r\n");
        messageField.setText(null);
    }

    // 构造方法
    public Server() {
        frame = new JFrame("服务器");
        try { // 使用Windows的界面风格
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        generalFont = new Font("微软雅黑", Font.PLAIN, 20);
        UIManager.put("Button.font", generalFont);
        UIManager.put("TextField.font", generalFont);
        UIManager.put("TextArea.font", generalFont);
        UIManager.put("TextArea.background", new Color(240, 240, 240));
        UIManager.put("List.foreground", Color.BLACK);
        UIManager.put("List.background", new Color(0, 0, 0, 0));
        UIManager.put("List.selectionForeground", Color.white);
        UIManager.put("List.selectionBackground", new Color(0, 0, 0, 150));
        UIManager.put("List.font", generalFont);
        UIManager.put("Label.font", generalFont);

        contentArea = new JTextArea();
        contentArea.setEditable(false);
        messageField = new JTextField();
        maxField = new JTextField("10", 2);
        startButton = new JButton("启动");
        stopButton = new JButton("停止");
        sendButton = new JButton("发送");
        stopButton.setEnabled(false);
        listModel = new DefaultListModel<String>();
        userList = new JList<String>(listModel);

        southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "写消息", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, generalFont, Color.blue));
        southPanel.add(messageField, "Center");
        southPanel.add(sendButton, "East");
        leftScroll = new JScrollPane(userList);
        leftScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "在线用户", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, generalFont, Color.blue));
        sendButton.setOpaque(false);
        startButton.setOpaque(false);
        stopButton.setOpaque(false);

        rightPanel = new JPanel(new BorderLayout());
        rightScroll = new JScrollPane(contentArea);
        contentArea.setOpaque(false);
        rightPanel.add(rightScroll, "Center");
        rightPanel.add(southPanel, "South");
        rightScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "消息显示区", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, generalFont, Color.blue));

        centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, rightPanel, leftScroll);
        centerSplit.setOpaque(false);
        rightPanel.setOpaque(false);
        leftScroll.setOpaque(false);
        leftScroll.getViewport().setOpaque(false);
        rightScroll.setOpaque(false);
        rightScroll.getViewport().setOpaque(false);
        southPanel.setOpaque(false);
        userList.setOpaque(false);
        centerSplit.setDividerLocation(600);

        northPanel = new JPanel(new BorderLayout());
        northPanelLeft = new JPanel(new FlowLayout());
        northPanelLeft.add(new JLabel("人数上限"));
        northPanelLeft.add(maxField);
        northPanelRight = new JPanel(new FlowLayout());
        northPanelRight.add(startButton);
        northPanelRight.add(stopButton);
        northPanel.add(northPanelLeft, "West");
        northPanel.add(northPanelRight, "East");
        northPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "配置信息", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, generalFont, Color.blue));
        northPanel.setOpaque(false);
        northPanelLeft.setOpaque(false);
        northPanelRight.setOpaque(false);

        kickMenu = new JPopupMenu();
        kick = new JMenuItem("强制下线");
        kickMenu.add(kick);

        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setFixedCellHeight(25);
        userList.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && e.getY() < userList.getFixedCellHeight() * (userList.getSelectedIndex() + 1)
                        && e.getY() > userList.getFixedCellHeight() * userList.getSelectedIndex())
                    kickMenu.show(userList, e.getX(), e.getY());
            }
        });

        BackgroundPanel panel = new BackgroundPanel(Toolkit.getDefaultToolkit().createImage("img/serverBackGround.png"));
        panel.setLayout(new BorderLayout());
        panel.add(northPanel, "North");
        panel.add(centerSplit, "Center");
        frame.add(panel);
        frame.setSize(800, 600);
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setLocation((screen_width - frame.getWidth()) / 2,
                (screen_height - frame.getHeight()) / 2);
        frame.setVisible(true);

        // 关闭窗口时事件
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (isStart) {
                    closeServer();// 关闭服务器
                }
                System.exit(0);// 退出程序
            }
        });

        // 文本框按回车键时事件
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });

        // 单击发送按钮时事件
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });

        // 单击启动服务器按钮时事件
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isStart) {
                    JOptionPane.showMessageDialog(frame, "服务器已处于启动状态，不要重复启动！", "错误", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                int max;
                try {
                    try {
                        max = Integer.parseInt(maxField.getText());
                    } catch (Exception e1) {
                        throw new Exception("人数上限为正整数！");
                    }
                    if (max <= 0) {
                        throw new Exception("人数上限为正整数！");
                    }
                    serverStart(max);
                    contentArea.append("服务器已成功启动!  人数上限：" + max + "\r\n");
                    JOptionPane.showMessageDialog(frame, "服务器成功启动!", "提示", JOptionPane.PLAIN_MESSAGE);
                    startButton.setEnabled(false);
                    maxField.setEnabled(false);
                    stopButton.setEnabled(true);
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(frame, exc.getMessage(), "错误", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        // 单击停止服务器按钮时事件
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isStart) {
                    JOptionPane.showMessageDialog(frame, "服务器还未启动，无需停止！", "错误", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                try {
                    closeServer();
                    startButton.setEnabled(true);
                    maxField.setEnabled(true);
                    stopButton.setEnabled(false);
                    contentArea.append("服务器成功停止!\r\n");
                    JOptionPane.showMessageDialog(frame, "服务器成功停止！", "提示", JOptionPane.PLAIN_MESSAGE);
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(frame, "停止服务器发生异常！", "错误", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        // 强制下线
        kick.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ClientThread ct = clients.get(userList.getSelectedIndex());
                ct.writer.println("COMMAND@KICK@");
                ct.writer.flush();
            }
        });
    }

    // 启动服务器
    public void serverStart(int max) throws java.net.BindException {
        try {
            clients = new ArrayList<>();
            users = new HashMap<>();
            onlineUsers = new HashMap<>();
            groups = new HashMap<>();
            users.put("1", new User("1", "1", null));
            users.put("2", new User("2", "2", null));
            groups.put("0", new Group("用户广播", "0"));
            serverSocket = new ServerSocket(6666);
            serverThread = new ServerThread(serverSocket, max);
            serverThread.start();
            isStart = true;
        } catch (BindException e) {
            isStart = false;
            throw new BindException("端口号已被占用，请换一个！");
        } catch (Exception e1) {
            e1.printStackTrace();
            isStart = false;
            throw new BindException("启动服务器异常！");
        }
    }

    // 关闭服务器
    public void closeServer() {
        try {
            if (serverThread != null)
                serverThread.stop();// 停止服务器线程

            for (int i = clients.size() - 1; i >= 0; i--) {
                // 给所有在线用户发送关闭命令
                clients.get(i).getWriter().println("COMMAND@CLOSE");
                clients.get(i).getWriter().flush();
                // 释放资源
                clients.get(i).stop();// 停止此条为客户端服务的线程
                clients.get(i).reader.close();
                clients.get(i).writer.close();
                clients.get(i).socket.close();
                clients.remove(i);
            }
            if (serverSocket != null) {
                serverSocket.close();// 关闭服务器端连接
            }
            listModel.removeAllElements();// 清空用户列表
            isStart = false;
        } catch (IOException e) {
            e.printStackTrace();
            isStart = true;
        }
    }

    // 群发服务器消息
    public void sendServerMessage(String message) {
        for (int i = clients.size() - 1; i >= 0; i--) {
            clients.get(i).getWriter().println("MESSAGE@ALL@服务器" + "@" + message);
            clients.get(i).getWriter().flush();
        }
    }

    // 服务器线程
    class ServerThread extends Thread {
        private ServerSocket serverSocket;
        private int max;// 人数上限

        // 服务器线程的构造方法
        public ServerThread(ServerSocket serverSocket, int max) {
            this.serverSocket = serverSocket;
            this.max = max;
        }

        public void run() {
            while (isStart) {// 不停的等待客户端的链接
                try {
                    Socket socket = serverSocket.accept();
                    BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter w = new PrintWriter(socket.getOutputStream());
                    new Thread() {
                        public void run() {
                            try {//负责客户端的注册与登录操作
                                boolean isDone = false;
                                while (!isDone) {
                                    // 接收客户端的基本用户信息
                                    String inf = r.readLine();
                                    StringTokenizer st = new StringTokenizer(inf, "@");
                                    String command = st.nextToken();
                                    String account = st.nextToken();
                                    String password = st.nextToken();
                                    if (command.equals("REGISTER")) {
                                        if (!users.containsKey(account)) {
                                            User user = new User(account, password, null);
                                            users.put(account, user);
                                            w.println("SUCCESS");
                                            w.flush();
                                        } else {
                                            w.println("DUPLICATED");
                                            w.flush();
                                        }
                                    } else if (command.equals("LOGIN")) {
                                        if (clients.size() == max) {// 如果已达人数上限
                                            // 反馈服务器满信息
                                            w.println("MAX");
                                            w.flush();
                                            isDone = true;
                                            // 释放资源
                                            r.close();
                                            w.close();
                                            socket.close();
                                        } else if (users.containsKey(account) && !onlineUsers.containsKey(account)) {
                                            if (users.get(account).getPassword().equals(password)) {
                                                w.println("SUCCESS");
                                                w.flush();
                                                String nickName = r.readLine();
                                                isDone = true;
                                                users.get(account).setNickName(nickName);
                                                onlineUsers.put(account, users.get(account));
                                                ClientThread client = new ClientThread(socket, users.get(account));
                                                client.start();// 开启对此客户端服务的线程
                                                clients.add(client);
                                                listModel.addElement(client.getUser().getNickName() + " (" + client.getUser().getAccount() + ")");// 更新在线列表
                                                contentArea.append(client.getUser().getNickName() + " (" + client.getUser().getAccount() + ")" + "上线!\r\n");

                                            } else {
                                                w.println("ERROR");
                                                w.flush();
                                            }
                                        } else if (onlineUsers.containsKey(account)) {
                                            w.println("DUPLICATED");
                                            w.flush();
                                        } else {
                                            w.println("ERROR");
                                            w.flush();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 为一个客户端服务的线程
    class ClientThread extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private User user;

        public BufferedReader getReader() {
            return reader;
        }

        public PrintWriter getWriter() {
            return writer;
        }

        public User getUser() {
            return user;
        }

        // 客户端线程的构造方法
        public ClientThread(Socket socket, User user) {
            try {
                this.socket = socket;
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream());
                this.user = user;
                // 反馈当前在线用户信息
                if (clients.size() > 0) {
                    String temp = "";
                    for (int i = clients.size() - 1; i >= 0; i--) {
                        temp += (clients.get(i).getUser().getAccount() + "@" + clients.get(i).getUser().getNickName() + "@");
                    }
                    writer.println("COMMAND@USERLIST@" + clients.size() + "@" + temp);
                    writer.flush();
                }
                // 向所有在线用户发送该用户上线命令
                for (int i = clients.size() - 1; i >= 0; i--) {
                    System.out.println("COMMAND@ADD@" + user.getAccount() + "@" + user.getNickName());
                    clients.get(i).getWriter().println("COMMAND@ADD@" + user.getAccount() + "@" + user.getNickName());
                    clients.get(i).getWriter().flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {// 不断接收客户端的消息，进行处理。
            String message = null;
            while (true) {
                try {
                    message = reader.readLine();// 接收客户端消息
                    System.out.println(message);
                    StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
                    String type = stringTokenizer.nextToken();
                    if (type.equals("COMMAND")) {
                        String command = stringTokenizer.nextToken();
                        switch (command) {
                            case "CLOSE": { // 下线命令
                                contentArea.append(this.getUser().getNickName() + " (" + this.getUser().getAccount() + ")" + "下线！\r\n");
                                // 断开连接释放资源
                                reader.close();
                                writer.close();
                                socket.close();
                                // 向所有在线用户发送该用户的下线命令
                                for (int i = clients.size() - 1; i >= 0; i--) {
                                    clients.get(i).getWriter().println("COMMAND@DELETE@" + user.getAccount());
                                    clients.get(i).getWriter().flush();
                                }
                                listModel.removeElement(this.getUser().getNickName() + " (" + this.getUser().getAccount() + ")");// 更新在线列表
                                onlineUsers.remove(this.getUser().getAccount());
                                // 删除此条客户端服务线程
                                for (int i = clients.size() - 1; i >= 0; i--) {
                                    if (clients.get(i).getUser() == user) {
                                        ClientThread temp = clients.get(i);
                                        clients.remove(i);// 删除此用户的服务线程
                                        temp.stop();// 停止这条服务线程
                                        return;
                                    }
                                }
                            }
                            case "ADDFRIEND": {// 加好友命令
                                String source = stringTokenizer.nextToken();
                                String target = stringTokenizer.nextToken();
                                dispatchMessage("COMMAND@" + source + "@" + target + "@" + "ADDFRIEND");
                                break;
                            }
                            case "FRIENDAGREED": {// 同意加好友命令
                                String source = stringTokenizer.nextToken();
                                String target = stringTokenizer.nextToken();
                                dispatchMessage("COMMAND@" + source + "@" + target + "@" + "FRIENDAGREED");
                                break;
                            }
                            case "ENDFRIEND": {// 删除好友命令
                                String source = stringTokenizer.nextToken();
                                String target = stringTokenizer.nextToken();
                                dispatchMessage("COMMAND@" + source + "@" + target + "@" + "ENDFRIEND");
                                break;
                            }
                            case "CREATEGROUP": {// 创建群聊
                                String source = stringTokenizer.nextToken();
                                String groupName = stringTokenizer.nextToken();
                                String groupAccount;
                                do {
                                    groupAccount = Integer.toString((int) (100000 + Math.random() * 899999));
                                } while (groups.containsKey(groupAccount));
                                Group group = new Group(groupName, groupAccount);
                                group.getMembers().add(source);
                                groups.put(groupAccount, group);
                                for (int i = clients.size() - 1; i >= 0; i--) {
                                    if (clients.get(i).getUser().getAccount().equals(source)) {
                                        System.out.println("COMMAND@CREATEGROUP@" + groupAccount + "@" + groupName);
                                        clients.get(i).getWriter().println("COMMAND@CREATEGROUP@" + groupAccount + "@" + groupName);
                                        clients.get(i).getWriter().flush();
                                        break;
                                    }
                                }
                                break;
                            }
                            case "QUITGROUP": {// 退群命令
                                String source = stringTokenizer.nextToken();
                                String target = stringTokenizer.nextToken();
                                groups.get(target).getMembers().remove(source);
                                if (groups.get(target).getMembers().isEmpty()) {
                                    groups.remove(target);
                                } else {
                                    dispatchMessage("COMMAND@" + source + "@" + target + "@" + "QUITGROUP");
                                }
                                break;
                            }
                            case "JOINGROUP": {// 加群命令
                                String source = stringTokenizer.nextToken();
                                String target = stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                String groupName = stringTokenizer.nextToken();
                                dispatchMessage("COMMAND@" + source + "@" + target + "@" + "JOINGROUP" +
                                        "@" + groupAccount + "@" + groupName);
                                break;
                            }
                            case "JOINAGREED": {// 同意加群
                                String source = stringTokenizer.nextToken();
                                String target = stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                groups.get(groupAccount).getMembers().add(source);
                                dispatchMessage("COMMAND@" + source + "@" + target + "@" + "JOINAGREED" +
                                        "@" + groupAccount);
                                break;
                            }
                            case "UPDATEGROUP": {// 更新群成员
                                String source = stringTokenizer.nextToken();
                                String target = stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                String memberSize = stringTokenizer.nextToken();
                                String members = stringTokenizer.nextToken();
                                groups.get(groupAccount).getMembers().add(source);
                                dispatchMessage("COMMAND@" + source + "@" + target + "@" + "UPDATEGROUP" +
                                        "@" + groupAccount + "@" + memberSize + "@" + members);
                                break;
                            }
                        }
                    } else if (type.equals("MESSAGE")) {
                        dispatchMessage(message);// 转发消息
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 转发消息
        public void dispatchMessage(String message) {
            StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
            String type = stringTokenizer.nextToken();
            String source = stringTokenizer.nextToken();
            String target = stringTokenizer.nextToken();
            String content = stringTokenizer.nextToken();
            if (type.equals("COMMAND")) {
                if (!content.equals("QUITGROUP") && !content.equals("JOINGROUP")
                        && !content.equals("JOINAGREED") && !content.equals("UPDATEGROUP")) {
                    message = "COMMAND@" + content + "@" + source;
                    for (int i = clients.size() - 1; i >= 0; i--) {
                        if (clients.get(i).getUser().getAccount().equals(target)) {
                            clients.get(i).getWriter().println(message);
                            clients.get(i).getWriter().flush();
                        }
                    }
                } else {
                    switch (content) {
                        case "QUITGROUP": {
                            message = "COMMAND@" + content + "@" + source + "@" + target;
                            for (int i = clients.size() - 1; i >= 0; i--) {
                                if (groups.get(target).getMembers().contains(clients.get(i).getUser().getAccount())) {
                                    clients.get(i).getWriter().println(message);
                                    clients.get(i).getWriter().flush();
                                }
                            }
                            break;
                        }
                        case "JOINGROUP": {
                            String groupAccount = stringTokenizer.nextToken();
                            String groupName = stringTokenizer.nextToken();
                            message = "COMMAND@" + content + "@" + source + "@" + groupAccount + "@" + groupName;
                            for (int i = clients.size() - 1; i >= 0; i--) {
                                if (clients.get(i).getUser().getAccount().equals(target)) {
                                    clients.get(i).getWriter().println(message);
                                    clients.get(i).getWriter().flush();
                                }
                            }
                            break;
                        }
                        case "JOINAGREED": {
                            String groupAccount = stringTokenizer.nextToken();
                            message = "COMMAND@" + content + "@" + source + "@" + groupAccount;
                            for (int i = clients.size() - 1; i >= 0; i--) {
                                if (clients.get(i).getUser().getAccount().equals(target)) {
                                    clients.get(i).getWriter().println(message);
                                    clients.get(i).getWriter().flush();
                                }
                            }
                            break;
                        }
                        case "UPDATEGROUP": {
                            String groupAccount = stringTokenizer.nextToken();
                            String memberSize = stringTokenizer.nextToken();
                            String members = stringTokenizer.nextToken();
                            message = "COMMAND@" + content + "@" + source + "@" + groupAccount + "@" + memberSize + "@" + members;
                            for (int i = clients.size() - 1; i >= 0; i--) {
                                if (groups.get(groupAccount).getMembers().contains(clients.get(i).getUser().getAccount())) {
                                    clients.get(i).getWriter().println(message);
                                    clients.get(i).getWriter().flush();
                                }
                            }
                            break;
                        }
                    }
                }

            } else if (type.equals("MESSAGE")) {
                if (target.equals("ALL")) {// 群发
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = df.format(new Date());
                    message = "MESSAGE@ALL@" + source + "@" + content;
                    contentArea.append(source + "      " + time + "\r\n"  + content + "\r\n");
                    for (int i = clients.size() - 1; i >= 0; i--) {
                        clients.get(i).getWriter().println(message);
                        clients.get(i).getWriter().flush();
                    }
                } else if (onlineUsers.containsKey(target)) {
                    message = "MESSAGE@PERSONAL@" + source + "@" + content;
                    for (int i = clients.size() - 1; i >= 0; i--) {
                        if (clients.get(i).getUser().getAccount().equals(target)) {
                            clients.get(i).getWriter().println(message);
                            clients.get(i).getWriter().flush();
                        }
                    }
                } else {
                    message = "MESSAGE@GROUP@" + source + "@" + target + "@" + content;
                    for (int i = clients.size() - 1; i >= 0; i--) {
                        if (groups.get(target).getMembers().contains(clients.get(i).getUser().getAccount())) {
                            clients.get(i).getWriter().println(message);
                            clients.get(i).getWriter().flush();
                        }
                    }
                }
            }
        }
    }
}