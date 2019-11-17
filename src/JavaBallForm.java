import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class JavaBallForm {

    private JFrame frameMain;
    private JPanel panelMain;
    private JButton removeTeamButton;
    private JButton getResultsButton;
    private JButton showRankedTableButton;
    private JButton exitButton;
    private JScrollPane scrollPane;
    private JTable matchTable;
    private JButton addResultButton;


    private static Dimension dimension = new Dimension(700, 700);
    private final String noResults = "***no Results yet***";

    public static ArrayList<Ranking> rankingList = new ArrayList<>();


    public JavaBallForm() {

        getResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTeamButton.setEnabled(false);
                getResultsButton.setEnabled(false);
                addResultButton.setEnabled(true);
                try {
                    JavaBall.readFile(JavaBall.resultList, "ResultsIn.txt", false);
                } catch (FileNotFoundException e1) {
                    showTerminatingDialog("File not found");
                }

                DefaultTableModel tableModel = ((DefaultTableModel) matchTable.getModel());
                for (String result : JavaBall.resultList) {
                    String newMatch;
                    String newResult;
                    String[] teams = result.split(" ");
                    int compare = teams[0].compareToIgnoreCase(teams[2]);
                    if (compare < 0) {
                        newMatch = teams[0] + " v " + teams[2];
                        newResult = result;
                    } else {
                        newMatch = teams[2] + " v " + teams[0];
                        newResult = teams[2] + " " + teams[3] + " " + teams[0] + " " + teams[1];
                    }

                    for (int j = 0; j < tableModel.getRowCount(); j++) {
                        if (newMatch.equals(tableModel.getValueAt(j, 0))) {
                            tableModel.setValueAt(newResult, j, 0);
                            tableModel.setValueAt("", j, 1);
                            tableModel.fireTableCellUpdated(j, 0);
                            tableModel.fireTableCellUpdated(j, 1);
                        }
                    }

                }
                if (areAllResultsAdded()) {
                    showRankedTableButton.setEnabled(true);
                }
            }
        });

        addResultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel tableModel = ((DefaultTableModel) matchTable.getModel());
                for (int j = 0; j < tableModel.getRowCount(); j++) {
                    if (matchTable.isRowSelected(j) && !tableModel.getValueAt(j, 1).toString().isEmpty()) {
                        JTextField team1 = new JTextField();
                        JTextField team2 = new JTextField();
                        String[] teams = tableModel.getValueAt(j, 0).toString().split(" ");
                        Object[] message = {
                                teams[0], team1,
                                teams[2], team2
                        };

                        int option = JOptionPane.showConfirmDialog(null, message, "Add Result", JOptionPane.OK_CANCEL_OPTION);
                        if (option == JOptionPane.OK_OPTION) {
                            try {
                                int score1 = Integer.parseInt(team1.getText());
                                int score2 = Integer.parseInt(team2.getText());
                                String newResult;

                                if (score1 >= 0 && score1 <= 9 && score2 >= 0 && score2 <= 9) {
                                    newResult = teams[0] + " " + score1 + " " + teams[2] + " " + score2;
                                    tableModel.setValueAt(newResult, j, 0);
                                    tableModel.setValueAt("", j, 1);
                                    tableModel.fireTableCellUpdated(j, 0);
                                    tableModel.fireTableCellUpdated(j, 1);
                                } else {
                                    JOptionPane.showMessageDialog(frameMain, "Invalid Result. Scores must be single digits.", "Warning", JOptionPane.WARNING_MESSAGE);
                                }
                            } catch (NumberFormatException x) {
                                JOptionPane.showMessageDialog(frameMain, "Invalid Result. Please add integers.", "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    } else if (matchTable.isRowSelected(j) && tableModel.getValueAt(j, 1).toString().isEmpty()) {
                        JOptionPane.showMessageDialog(frameMain, "The result is already added", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
                if (matchTable.getSelectionModel().isSelectionEmpty()) {
                    JOptionPane.showMessageDialog(frameMain, "You have to select a team", "Warning", JOptionPane.WARNING_MESSAGE);
                }
                if (areAllResultsAdded()) {
                    showRankedTableButton.setEnabled(true);
                }
            }
        });

        removeTeamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem menuItem;
                for (String team : JavaBall.teamList) {
                    menuItem = new JMenuItem(team);
                    menuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove " + team + " from the tournament?", "Confirmation", JOptionPane.YES_NO_OPTION);
                            if (dialogResult == 0) {  //If Yes button is clicked
                                JavaBall.removeTeam(team);
                                DefaultTableModel tableModel = ((DefaultTableModel) matchTable.getModel());
                                int i = 0;
                                while (i < tableModel.getRowCount()) {
                                    if (tableModel.getValueAt(i, 0).toString().contains(team)) {
                                        tableModel.removeRow(i);
                                        tableModel.fireTableDataChanged();
                                    } else {
                                        i++;
                                    }
                                }
                            }

                            popupMenu.setVisible(false);

                            if (JavaBall.teamList.size() < 3) {
                                showTerminatingDialog("Tournament has been cancelled");
                            }
                        }
                    });
                    popupMenu.add(menuItem);
                }

                popupMenu.show(removeTeamButton, 0, removeTeamButton.getHeight());

                if (areAllResultsAdded()) {
                    showRankedTableButton.setEnabled(true);
                }
            }
        });

        showRankedTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rankingList.size() == 0) {
                    initializeRankingList();
                }

                DefaultTableModel tableModel = new NonEditableTableModel(0, 10);
                Object[] headersRow = {"", "", "Matches", "", "", "Goals", "", "Match", "Goal", ""};
                Object[] columnTitlesRow = {"Team", "Rank", "Won", "Draw", "Lost", "For", "Against", "Points", "Diff", "Medal"};
                tableModel.addRow(headersRow);
                tableModel.addRow(columnTitlesRow);
                for (Ranking ranking : rankingList) {
                    Object[] row = {ranking.getTeamName(), ranking.getRank(), ranking.getMatchesWon(), ranking.getMatchesDrawn(),
                            ranking.getMatchesLost(), ranking.getGoalsFor(), ranking.getGoalsAgainst(), ranking.getPoints(), ranking.getGoalDiff(),
                            ranking.getMedal()};
                    tableModel.addRow(row);
                }
                JTable rankingTable = new JTable(tableModel);
                rankingTable.setRowSelectionAllowed(false);
                JDialog dialog = new JDialog();
                JPanel panel = new JPanel();
                panel.add(rankingTable);
                dialog.add(panel);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setLocation(350, 400);
                dialog.setVisible(true);
                dialog.pack();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!areAllResultsAdded()) {
                    DefaultTableModel tableModel = ((DefaultTableModel) matchTable.getModel());
                    try {
                        FileWriter writer = new FileWriter("ResultsOut.txt");
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            writer.write(tableModel.getValueAt(i, 0).toString() + "\n");
                        }
                        writer.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    if (rankingList.size() == 0) {
                        initializeRankingList();
                    }
                    try {
                        FileWriter writer = new FileWriter("ResultsOut.txt");
                        writer.write(createHeadersForTable() + "\n");
                        writer.write(createColumnTitlesForTable() + "\n");
                        for (Ranking ranking : rankingList) {
                            writer.write(ranking.createStringLineforTable() + "\n");
                        }
                        writer.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });

    }

    public void setFrame() {
        frameMain = new JFrame("JavaBall");
        frameMain.setContentPane(new JavaBallForm().panelMain);
        frameMain.setPreferredSize(dimension);
        frameMain.setLocation(400, 100);
        frameMain.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frameMain.setResizable(true);
        frameMain.pack();
        frameMain.setVisible(true);

    }

    public void showTerminatingDialog(String message) {
        JOptionPane.showMessageDialog(frameMain, message, "Warning", JOptionPane.WARNING_MESSAGE);

        System.exit(0); //exit from the program
    }


    private void createUIComponents() {
        ArrayList<String> matchList = JavaBall.createMatchList(JavaBall.teamList);

        DefaultTableModel tableModel = new NonEditableTableModel(0, 2);

        for (String match : matchList) {
            Object[] row = {match, noResults};
            tableModel.addRow(row);
        }
        matchTable = new JTable(tableModel);

        matchTable.setRowSelectionAllowed(true);
        matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private boolean areAllResultsAdded() {
        DefaultTableModel tableModel = ((DefaultTableModel) matchTable.getModel());
        boolean flag = true;
        for (int j = 0; j < tableModel.getRowCount(); j++) {
            if (!tableModel.getValueAt(j, 1).toString().isEmpty()) {
                flag = false;
            }
        }
        return flag;
    }

    private ArrayList<String> countWins() {
        DefaultTableModel tableModel = ((DefaultTableModel) matchTable.getModel());
        ArrayList<String> wins = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String[] scores = tableModel.getValueAt(i, 0).toString().split(" ");
            if (Integer.parseInt(scores[1]) > Integer.parseInt(scores[3])) {
                wins.add(scores[0]);
            } else if (Integer.parseInt(scores[1]) < Integer.parseInt(scores[3])) {
                wins.add(scores[2]);
            }
        }
        return wins;
    }

    private ArrayList<String> countDraws() {
        DefaultTableModel tableModel = ((DefaultTableModel) matchTable.getModel());
        ArrayList<String> draws = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String[] scores = tableModel.getValueAt(i, 0).toString().split(" ");
            if (Integer.parseInt(scores[1]) == Integer.parseInt(scores[3])) {
                draws.add(scores[0]);
            }
        }
        return draws;
    }

    private void sortRankingList() {
        Collections.sort(rankingList, new Comparator<Ranking>() {
            @Override
            public int compare(Ranking o1, Ranking o2) {
                if (o2.getPoints() == o1.getPoints()) {
                    return o2.getGoalDiff() - o1.getGoalDiff();
                } else {
                    return o2.getPoints() - o1.getPoints();
                }
            }
        });
    }

    public void initializeRankingList() {
        giveTeamNames(JavaBall.teamList);
        giveMatches();
        giveGoals();
        sortRankingList();
        giveRankingsAndMedals();
    }

    public void giveTeamNames(ArrayList<String> teamList) {
        for (int i = 0; i < teamList.size(); i++) {
            Ranking ranking = new Ranking();
            ranking.setTeamName(teamList.get(i));
            rankingList.add(ranking);
        }
    }


    private void giveRankingsAndMedals() {
        int rank = 1;
        rankingList.get(0).setRank(rank);
        rankingList.get(0).setMedal("Gold");
        int i = 1;
        while (i < rankingList.size()) {
            if (rankingList.get(i).getPoints() == rankingList.get(i - 1).getPoints() && rankingList.get(i).getGoalDiff() == rankingList.get(i - 1).getGoalDiff()) {
                rankingList.get(i).setRank(rank);
                rankingList.get(i).setMedal(rankingList.get(i-1).getMedal());
                rank++;
            } else {
                rank++;
                rankingList.get(i).setRank(rank);
                if (rankingList.get(i-1).getMedal().equals("Gold")){
                    rankingList.get(i).setMedal("Silver");
                } else if (rankingList.get(i-1).getMedal().equals("Silver")) {
                    rankingList.get(i).setMedal("Bronze");
                } else if (rankingList.get(i-1).getMedal().equals("Bronze")) {
                    rankingList.get(i).setMedal("");
                } else {
                    rankingList.get(i).setMedal("");
                }
            }
            i++;
        }
    }

    private void giveMatches() {
        for (Ranking ranking : rankingList) {
            int numberOfWins = 0;
            int numberOfDraws = 0;

            for (String win : countWins()) {
                if (win.equals(ranking.getTeamName())) {
                    ranking.addWin();
                    numberOfWins++;
                }
            }
            for (String draw : countDraws()) {
                if (draw.equals(ranking.getTeamName())) {
                    ranking.addDraw();
                    numberOfDraws++;
                }
            }
            ranking.setMatchesWon(numberOfWins);
            ranking.setMatchesDrawn(numberOfDraws);
            ranking.setMatchesLost((JavaBall.teamList.size() - 1) - (numberOfWins + numberOfDraws));
        }
    }

    public void giveGoals() {
        DefaultTableModel tableModel = ((DefaultTableModel) matchTable.getModel());

        for (Ranking ranking : rankingList) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String[] scores = tableModel.getValueAt(i, 0).toString().split(" ");
                if (ranking.getTeamName().equals(scores[0])) {
                    ranking.addGoalsFor(Integer.parseInt(scores[1]));
                    ranking.addGoalsAgainst(Integer.parseInt(scores[3]));
                } else if (ranking.getTeamName().equals(scores[2])) {
                    ranking.addGoalsFor(Integer.parseInt(scores[3]));
                    ranking.addGoalsAgainst(Integer.parseInt(scores[1]));
                }
            }
            ranking.calculateGoalDifference();
        }
    }

    public String createColumnTitlesForTable() {
        String format = "%-15s%-10s%-10s%-10s%-10s%-10s%-10s%-10s%-10s%-10s%n";
        return String.format(format, "Team", "Rank", "Won", "Draw", "Lost", "For", "Against", "Points", "Diff", "Medal");
    }

    public String createHeadersForTable() {
        String format = "%50s%14s%16s%9s%n";
        return String.format(format, "Matches", "Goals", "Match", "Goal");
    }


}
