package zx.ias.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedList;

/**
 * DataChooserUI
 */

public class DateChooser extends JDialog implements ActionListener {

    private JComboBox yearBox, monthBox, dayBox;
    private int selectedDay;

    DateChooser(LocalDate date, DateChooserCallback callback) {

        setTitle("日期选择器");
        LocalDate now = LocalDate.now();
        setLayout(new GridLayout(2, 1));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        LinkedList<Integer> years, months, days;
        years = new LinkedList<>();
        months = new LinkedList<>();
        days = new LinkedList<>();

        /*年月日列表初始化*/
        for (int i = 1970; i <= now.getYear(); i++) {
            years.add(i);
        }
        for (int i = 1; i <= 12; i++) {
            months.add(i);
        }
        for (int i = 1; i <= date.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth(); i++) {
            days.add(i);
        }

        /*初始化下拉列表框*/
        yearBox = new JComboBox<>(years.toArray());
        monthBox = new JComboBox<>(months.toArray());
        dayBox = new JComboBox<>(days.toArray());

        /*定位选中选项*/
        selectedDay = date.getDayOfMonth();
        yearBox.setSelectedItem(date.getYear());
        monthBox.setSelectedItem(date.getMonthValue());
        dayBox.setSelectedItem(selectedDay);

        /*时间监听*/
        yearBox.addActionListener(this);
        monthBox.addActionListener(this);
        dayBox.addActionListener(this);

        JPanel subTitlePanel = new JPanel();
        subTitlePanel.add(new JLabel("(年-月-日)关闭返回结果"));

        JPanel panel = new JPanel();
        panel.add(yearBox);
        panel.add(monthBox);
        panel.add(dayBox);

        add(subTitlePanel);
        add(panel);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        /*关闭执行回调*/
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                callback.onDateChosen(LocalDate.of((int) yearBox.getSelectedItem(), (int) monthBox.getSelectedItem(), (int) dayBox.getSelectedItem()));
            }
        });
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src.equals(yearBox) || src.equals(monthBox)) {
            /*必须要先移除监听器才能列表项*/
            dayBox.removeActionListener(this);
            dayBox.removeAllItems();
            /*按照当月天数添加日列表项*/
            for (int i = 1; i <= LocalDate
                    .of((int) yearBox.getSelectedItem(), (int) monthBox.getSelectedItem(), 1)
                    .with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth(); i++) {
                dayBox.addItem(i);
            }
            dayBox.setSelectedItem(selectedDay);
            dayBox.addActionListener(this);
        } else if (src.equals(dayBox)) {
            /*缓存选中日数*/
            Object selected = dayBox.getSelectedItem();
            selectedDay = selected != null ? (int) selected : 1;
        }
    }
}
