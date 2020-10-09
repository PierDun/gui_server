import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final int PORT = 1408;
    private static final String PATH = "C:\\Users\\User\\Desktop\\gui_server\\Password.txt";
    private static ChestCollection curSet = new ChestCollection();

    public static void main(String[] args) {
        Socket socket;
        SwingUtilities.invokeLater(ServerGUI::new);
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                System.out.println("Ожидание подключения...");
                socket = serverSocket.accept();
                System.out.println("Подключился клиент: " + socket.getInetAddress());
                new Thread(new ComThread(curSet, socket)).start();
            }

        } catch(IOException e){
            e.printStackTrace();
        }
    }

    static class ServerGUI extends JFrame implements ActionListener {

        JTextField passwordTextField;
        JLabel passwordCheckLabel;
        JFrame loginFrame;
        static JFrame mainFrame;
        DynamicTree treePanel;
        Chest selectedChest;
        CommandType commandType;

        private ServerGUI() {
            super("Сервер");
            login();
        }

        void darkenFrame(){
            mainFrame.getRootPane().setGlassPane(new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    g.setColor(new Color(0,0,0,100));
                    g.fillRect(0,0, mainFrame.getWidth(), mainFrame.getHeight());
                    super.paintComponent(g);
                }
            });

            mainFrame.getGlassPane().setVisible(true);
        }

        static void resetFrame(){
            mainFrame.getGlassPane().setVisible(false);
        }

        private void login(){
            loginFrame = new JFrame("Вход");
            loginFrame.setLocationByPlatform(true);
            loginFrame.setSize(300,150);
            loginFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            loginFrame.setMinimumSize(new Dimension(300,150));

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            passwordTextField = new JPasswordField(10);
            passwordTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            passwordTextField.setMaximumSize(new Dimension(150,20));
            passwordTextField.setActionCommand("textFieldAction");

            JButton enterButton = new JButton("Войти");

            passwordTextField.addActionListener(this);
            enterButton.addActionListener(this);

            JLabel greetingLabel = new JLabel("Добро пожаловать!");
            greetingLabel.setFont(new Font("Courier New", Font.ITALIC, 25));

            passwordCheckLabel = new JLabel(" ");

            greetingLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            passwordTextField.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            passwordCheckLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            enterButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);

            panel.add(greetingLabel);
            panel.add(passwordTextField);
            panel.add(passwordCheckLabel);
            panel.add(enterButton);

            loginFrame.add(panel);

            loginFrame.pack();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        }

        private void init() {
            mainFrame = new JFrame("Сервер");

            mainFrame.setLocationByPlatform(true);

            mainFrame.setMinimumSize(new Dimension(1200,400));
            mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            treePanel = new DynamicTree("Сундуки");

            JMenuBar menuBar = new JMenuBar();

            JMenu menu = new JMenu("File");
            menu.setForeground(Color.BLACK);
            JMenuItem read = new JMenuItem("Считать коллекцию");
            JMenuItem save = new JMenuItem("Сохранить коллекцию");
            menu.add(read);
            menu.add(save);
            menu.addSeparator();
            JMenuItem logout = new JMenuItem("Выйти");

            menu.add(logout);

            read.addActionListener(this);
            save.addActionListener(this);
            logout.addActionListener(this);

            menu.addMenuListener(new MenuListener() {
                @Override
                public void menuSelected(MenuEvent e) { }

                @Override
                public void menuDeselected(MenuEvent e) {
                    System.out.println("menu deselected");
                }

                @Override
                public void menuCanceled(MenuEvent e) {
                    System.out.println("menu canceled");
                }
            });

            menuBar.add(menu);
            menuBar.setForeground(Color.GRAY);

            JPanel buttonsFrame = new JPanel(new GridLayout(6,1));

            JButton addElement = new JButton("Добавить элемент");
            JButton editElement = new JButton("Редактировать элемент");
            JButton removeElement = new JButton("Удалить элемент");
            JButton addIfMax = new JButton("Добавить максимальный");
            JButton addIfMin = new JButton("Добавить минимальный");
            JButton removeGreater = new JButton("Удалить больше, чем");

            addElement.addActionListener(this);
            editElement.addActionListener(this);
            removeElement.addActionListener(this);
            addIfMax.addActionListener(this);
            addIfMin.addActionListener(this);
            removeGreater.addActionListener(this);


            buttonsFrame.add(addElement);
            buttonsFrame.add(editElement);
            buttonsFrame.add(removeElement);
            buttonsFrame.add(addIfMax);
            buttonsFrame.add(addIfMin);
            buttonsFrame.add(removeGreater);


            mainFrame.setJMenuBar(menuBar);
            mainFrame.add(treePanel);
            mainFrame.add(buttonsFrame, BorderLayout.EAST);

            mainFrame.pack();
            mainFrame.setLocationRelativeTo(loginFrame);
            mainFrame.setVisible(true);

        }

        void add(){
            JFrame addFrame;

            JLabel nameLabel = new JLabel("Название:");
            JLabel sumLabel = new JLabel("Сумма:");
            JLabel state0Label = new JLabel("Координата 1:");
            JLabel state1Label = new JLabel("Координата 2:");
            JLabel checkLabel = new JLabel(" ");

            JTextField nameTF = new JTextField(10);
            JTextField sumTF = new JTextField(10);
            JTextField state0TF = new JTextField(3);
            JTextField state1TF = new JTextField(3);

            addFrame = new JFrame("Элемент");
            addFrame.setSize(300, 300);
            addFrame.setResizable(false);
            addFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            addFrame.setLocationRelativeTo(mainFrame);

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            JPanel namePanel = new JPanel(new FlowLayout());
            JPanel sumPanel = new JPanel(new FlowLayout());
            JPanel state0Panel = new JPanel(new FlowLayout());
            JPanel state1Panel = new JPanel(new FlowLayout());
            JPanel state2Panel = new JPanel(new FlowLayout());

            namePanel.setSize(250, 40);
            sumPanel.setSize(250, 40);

            state0Panel.setSize(250, 40);
            state1Panel.setSize(250, 40);
            state2Panel.setSize(250, 40);

            JButton submitButton = new JButton("Добавить");

            namePanel.add(nameLabel);
            namePanel.add(nameTF);

            sumPanel.add(sumLabel);
            sumPanel.add(sumTF);

            state0Panel.add(state0Label);
            state0Panel.add(state0TF);

            state1Panel.add(state1Label);
            state1Panel.add(state1TF);

            switch(commandType){
                case EDIT:
                    nameTF.setText(selectedChest.getName());
                    sumTF.setText(String.valueOf(selectedChest.getSum()));
                    state0TF.setText(String.valueOf(selectedChest.x));
                    state1TF.setText(String.valueOf(selectedChest.y));
                    submitButton.setText("Редактировать");
                    break;

                case REMOVEGREATER:
                    submitButton.setText("Удалить");
                    break;

                default:
                    break;
            }

            namePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            sumLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            state0Label.setAlignmentX(Component.CENTER_ALIGNMENT);
            state1Label.setAlignmentX(Component.CENTER_ALIGNMENT);
            submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            checkLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            submitButton.addActionListener(e -> {
                if (nameTF.getText().equals("") || sumTF.getText().equals("") || state0TF.getText().equals("") || state1TF.getText().equals("")){
                    checkLabel.setText("Заполните поля");
                    checkLabel.setForeground(Color.RED);
                    if (nameTF.getText().equals("")) {nameTF.setBorder(BorderFactory.createLineBorder(Color.RED));}
                    if (sumTF.getText().equals("")) {sumTF.setBorder(BorderFactory.createLineBorder(Color.RED));}
                    if (state0TF.getText().equals("")) {state0TF.setBorder(BorderFactory.createLineBorder(Color.RED));}
                    if (state1TF.getText().equals("")) {state1TF.setBorder(BorderFactory.createLineBorder(Color.RED));}
                } else {

                    try {
                        String name = nameTF.getText();
                        String sum = sumTF.getText();
                        String state0 = state0TF.getText();
                        String state1 = state1TF.getText();

                        Chest addedChest = new Chest(name, sum, state0, state1);

                        switch (commandType){
                            case EDIT:
                                new InformationPane("Элемент успешно отредактирован", mainFrame, "OK");

                                selectedChest.setName(name);
                                selectedChest.setSum(sum);

                                break;

                            case ADDMIN:
                                if(curSet.lower(addedChest)){
                                    new InformationPane("Минимальный элемент успешно добавлен", mainFrame, "OK");
                                }else{
                                    new InformationPane("Элемент не является минимальным", mainFrame, "ERROR");
                                }

                                curSet.add_element(addedChest);
                                treePanel.addObject(addedChest);

                                break;

                            case ADDMAX:
                                if(curSet.higher(addedChest)){
                                    new InformationPane("Максимальный элемент успешно добавлен", mainFrame, "OK");
                                }else{
                                    new InformationPane("Элемент не является максимальным", mainFrame, "ERROR");
                                }

                                curSet.add_element(addedChest);
                                treePanel.addObject(addedChest);

                                break;

                            case REMOVEGREATER:
                                ArrayList<Chest> removed = curSet.removeGreater(addedChest);

                                for (Chest removedChest: removed ){
                                    treePanel.remove(removedChest);
                                }

                                new InformationPane("Элементы успешно удалены", mainFrame, "OK");

                                curSet.removeElement(treePanel.getCurrentChest());

                                break;

                            default:

                                if (!curSet.contains(addedChest)) {

                                    curSet.add_element(addedChest);
                                    treePanel.addObject(addedChest);
                                    new InformationPane("Элемент успешно добавлен", mainFrame, "OK");
                                }else {
                                    new InformationPane("Элемент уже существует", mainFrame, "ERROR");
                                }
                                break;
                        }

                        addFrame.dispose();

                    }catch(NumberFormatException ex){
                        checkLabel.setText("Введите число");
                        checkLabel.setForeground(Color.RED);
                        nameTF.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        sumTF.setBorder(BorderFactory.createLineBorder(Color.RED));
                    }
                }
            });

            addFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowIconified(WindowEvent wEvt) {
                    resetFrame();
                    ((JFrame)wEvt.getSource()).dispose();

                }

                @Override
                public void windowDeactivated(WindowEvent wEvt) {
                    resetFrame();
                    ((JFrame)wEvt.getSource()).dispose();
                }
            });

            contentPanel.add(namePanel);
            contentPanel.add(sumPanel);
            contentPanel.add(state0Panel);
            contentPanel.add(state1Panel);
            contentPanel.add(state2Panel);
            contentPanel.add(checkLabel);
            contentPanel.add(submitButton);

            addFrame.add(contentPanel);

            addFrame.setVisible(true);
            //addFrame.pack();
            addFrame.addWindowListener(new WindowAdapter() {

                @Override
                public void windowDeactivated(WindowEvent wEvt) {
                    ((JFrame) wEvt.getSource()).dispose();
                }

            });
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            switch (ae.getActionCommand()) {
                case "Войти":

                    try {
                        String enteredPassword = passwordTextField.getText();
                        Scanner hashIn = new Scanner(new File(PATH));
                        //int hcodePassFile = hashIn.nextInt();
                        String hcodePassFile = hashIn.next();
                        //System.out.println(passHash.hash(enteredPassword.concat(USERNAME)).equals(hcodePassFile));

                        if (enteredPassword.equals(hcodePassFile)) {
                            passwordCheckLabel.setText("Верный пароль");
                            passwordCheckLabel.setForeground(Color.GREEN);
                            passwordTextField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
                            loginFrame.dispose();
                            init();
                        } else {
                            passwordTextField.setText("");
                            passwordCheckLabel.setText("Неверный пароль");
                            passwordCheckLabel.setForeground(Color.RED);
                            passwordTextField.setBorder(BorderFactory.createLineBorder(Color.RED));
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    break;
                case "Считать коллекцию":

                    try {
                        curSet.readElements();
                        darkenFrame();
                        new InformationPane("Коллекция успешно считана", loginFrame, "OK");
                        treePanel.clear();
                        populateTree(treePanel);

                    } catch (Exception e) {
                        new InformationPane("Произошла ошибка", loginFrame, "ERROR");
                        e.printStackTrace();
                    }

                    break;
                case "Сохранить коллекцию":

                    try {
                        darkenFrame();
                        curSet.save();
                        new InformationPane("Коллекция успешно сохранена", loginFrame, "OK");

                    } catch (Exception e) {
                        new InformationPane("Произошла ошибка", loginFrame, "ERROR");
                    }

                    break;
                case "Выйти":

                    mainFrame.dispose();
                    login();

                    break;
                case "Удалить элемент":

                    curSet.removeElement(treePanel.getCurrentChest());
                    darkenFrame();
                    new InformationPane("Элемент успешно удален", loginFrame, "OK");

                    treePanel.removeCurrentNode();

                    break;
                case "Добавить элемент":

                    commandType = CommandType.ADD;
                    add();
                    darkenFrame();

                    break;
                case "Редактировать элемент":
                    selectedChest = treePanel.getCurrentChest();
                    commandType = CommandType.EDIT;
                    add();
                    darkenFrame();

                    break;
                case "Добавить максимальный":

                    commandType = CommandType.ADDMAX;
                    add();
                    darkenFrame();
                    break;
                case "Добавить минимальный":

                    commandType = CommandType.ADDMIN;
                    add();
                    darkenFrame();
                    break;
                case "Удалить больше, чем":

                    commandType = CommandType.REMOVEGREATER;
                    add();
                    darkenFrame();
                    break;
            }
        }

        void populateTree(DynamicTree treePanel){
            for ( Chest curChest: curSet.returnObjects()){
                treePanel.addObject(curChest);
            }
        }

    }
}