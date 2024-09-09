import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import com.toedter.calendar.JCalendar;

public class App {
    private static ArrayList<TodoItem> todos = new ArrayList<>();
    private static DefaultListModel<String> listModel = new DefaultListModel<>();
    private static String selectedDate = "";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Todo App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        JCalendar calendar = new JCalendar();
        JList<String> todoList = new JList<>(listModel);
        JTextField todoInput = new JTextField();
        JButton addButton = new JButton("추가");
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"일반", "우선순위", "긴급"});

        todoInput.setVisible(false);
        addButton.setVisible(false);
        typeComboBox.setVisible(false);

        calendar.getDayChooser().addPropertyChangeListener("day", evt -> {
            selectedDate = calendar.getDate().toString();
            loadTodosForSelectedDate();

            todoInput.setVisible(true);
            addButton.setVisible(true);
            typeComboBox.setVisible(true);
            calendar.repaint();
        });

        addButton.addActionListener(e -> {
            String title = todoInput.getText();
            String type = (String) typeComboBox.getSelectedItem();
            if (!title.isEmpty() && type != null) {
                todos.add(new TodoItem(selectedDate, "[" + type + "] " + title));
                listModel.addElement("[" + type + "] " + title);
                todoInput.setText("");
            }
        });

        todoList.setCellRenderer(new TodoListCellRenderer());

        todoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = todoList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String selectedTodo = listModel.getElementAt(index);
                        String[] options = {"수정", "삭제", "취소"};
                        int choice = JOptionPane.showOptionDialog(frame, "항목을 선택하세요:", selectedTodo,
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[2]);

                        if (choice == 0) {
                            String newTitle = JOptionPane.showInputDialog(frame, "수정할 내용을 입력하세요:", selectedTodo);
                            if (newTitle != null && !newTitle.isEmpty()) {
                                String type = selectedTodo.split("\\[")[1].split("\\]")[0];
                                todos.get(index).setTitle("[" + type + "] " + newTitle);
                                listModel.set(index, "[" + type + "] " + newTitle);
                            }
                        } else if (choice == 1) {
                            todos.remove(index);
                            listModel.remove(index);
                        }
                    }
                }
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(typeComboBox, BorderLayout.WEST);
        inputPanel.add(todoInput, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        frame.add(calendar, BorderLayout.NORTH);
        frame.add(new JScrollPane(todoList), BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void loadTodosForSelectedDate() {
        listModel.clear();
        for (TodoItem todo : todos) {
            if (todo.getDate().equals(selectedDate)) {
                listModel.addElement(todo.getTitle());
            }
        }
    }

    static class TodoItem {
        private String date;
        private String title;

        public TodoItem(String date, String title) {
            this.date = date;
            this.title = title;
        }

        public String getDate() {
            return date;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    static class TodoListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String text = value.toString();

            if (text.contains("[긴급]")) {
                label.setForeground(Color.RED); // 긴급 항목은 빨간색으로 표시
            } else if (text.contains("[우선순위]")) {
                label.setForeground(Color.BLUE);
            } else {
                label.setForeground(Color.BLACK); // 일반 항목은 검정색으로 표시
            }

            return label;
        }
    }
}
