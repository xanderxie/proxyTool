package tool.entertainment.proxy.frame;

import tool.entertainment.proxy.Transform;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * @author : xiezhidong
 * @date :  2019/6/15  16:24
 */
public class MainFrame extends JFrame {

    private String regex="^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$";
    private Pattern ipPattern = Pattern.compile(regex);

    public static void main(String[] args) {
        new MainFrame();
    }

    private MainFrame() {
        JPanel jp1 = new JPanel();
        JPanel jp2 = new JPanel();
        JPanel jp3 = new JPanel();

        JLabel source = new JLabel("监听");
        JLabel target = new JLabel("转发");
        JLabel symbol1 = new JLabel("：");
        JLabel symbol2 = new JLabel("：");

        JButton start = new JButton("启动");
        JButton stop = new JButton("关闭");

        JTextField sourceIpField = new JTextField(10);
        sourceIpField.setText("127.0.0.1");
        JTextField sourcePortField = new JTextField(3);
        sourcePortField.setText("8080");
        JTextField targetIpField = new JTextField(10);
        targetIpField.setText("127.0.0.1");
        JTextField targetPortField = new JTextField(3);
        targetPortField.setText("7010");

        this.setLayout(new GridLayout(3, 1));

        // 加入各个组件
        jp1.add(source);
        jp1.add(sourceIpField);
        jp1.add(symbol1);
        jp1.add(sourcePortField);

        jp2.add(target);
        jp2.add(targetIpField);
        jp2.add(symbol2);
        jp2.add(targetPortField);


        jp3.add(start);
        jp3.add(stop);

        // 加入到JFrame
        this.add(jp1);
        this.add(jp2);
        this.add(jp3);

        this.setSize(250, 150);
        this.setTitle("私服代理");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        start.addActionListener((event) -> {
            String sourceIp = sourceIpField.getText();
            boolean b = checkIp(sourceIp, "监听");
            if (!b) {
                return;
            }
            String port = sourcePortField.getText();
            Integer sourcePort = checkPort(port, "监听");
            if (sourcePort == null) {
                return;
            }
            String targetIp = targetIpField.getText();
            b = checkIp(targetIp, "转发");
            if (!b) {
                return;
            }
            port = targetPortField.getText();
            Integer targetPort = checkPort(port, "转发");
            if (targetPort == null) {
                return;
            }
            new Thread(()->Transform.listen(sourceIp, sourcePort, targetIp, targetPort)).start();
        });

        stop.addActionListener((e -> Transform.exit()));


    }

    private boolean checkIp(String ip, String type) {
        if (ip == null || !ipPattern.matcher(ip).matches()) {
            JOptionPane.showMessageDialog(this, type + "ip不正确，请重新输入");
            return false;
        }
        return true;
    }

    private Integer checkPort(String port, String type) {
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, type + "端口不正确，请重新输入");
            return null;
        }

    }
}
