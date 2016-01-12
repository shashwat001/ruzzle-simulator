package com.simulator;

import com.google.common.collect.ComparisonChain;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shashwat on 19/10/15.
 */
public class Solver
{
    private Cell[][] matrix;
    private HashSet<String> wordList;
    private List<RuzzlePattern> patternList;

    public enum READ_STYLE
    {
        WITH_SCORE,
        WITHOUT_SCORE
    }


    public List<RuzzlePattern> getPatternListByRuzzleOrder()
    {
        filterList();
        return patternList;
    }

    public List<RuzzlePattern> getPatternListSortedByPoints()
    {
        sortByPoints();
        filterList();
        return patternList;
    }

    public List<RuzzlePattern> getRandomPatternList()
    {
        long seed = System.nanoTime();
        Collections.shuffle(patternList,new Random(seed));
        filterList();
        return patternList;
    }

    Solver()
    {
        matrix = new Cell[4][];
        for (int i = 0; i < 4; i++)
        {
            matrix[i] = new Cell[4];
        }
    }


    public void solve(READ_STYLE readStyle)
    {
        System.out.println("Solving the words");
        patternList = new LinkedList<>();
        boolean[][] visited = new boolean[4][4];
        Vector<Pair> coorList = new Vector<>();
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                dfs(i, j, visited, "", coorList);
            }
        }
        if(readStyle.equals(READ_STYLE.WITH_SCORE))
            populateScore();
        System.out.println("Words Solving complete");
    }

    private void populateScore()
    {
        for (RuzzlePattern pattern : patternList)
        {
            int score = calculateScore(pattern.wordCoordinates);
            pattern.score = score;
        }
    }

    private int calculateScore(Vector<Pair> wordCoordinates)
    {
        int factor = 1;
        int score = 0;
        for (Pair pair : wordCoordinates)
        {
            Cell curCell = matrix[pair.x][pair.y];
            int localFactor = 1;
            if (curCell.cellMultiplier == Cell.MULTIPLIER.DW)
                factor = factor * 2;
            else if (curCell.cellMultiplier == Cell.MULTIPLIER.TW)
                factor = factor * 3;
            else if (curCell.cellMultiplier == Cell.MULTIPLIER.DL)
                localFactor = 2;
            else if (curCell.cellMultiplier == Cell.MULTIPLIER.TL)
                localFactor = 3;
            score = score + curCell.characterPoint * localFactor;
        }
        score = score * factor;
        score = score + getBonusLengthPoints(wordCoordinates.size());
        return score;
    }

    private int getBonusLengthPoints(int size)
    {
        if (size == 5)
            return 5;
        if (size == 6)
            return 10;
        if (size == 7)
            return 15;
        if (size == 8)
            return 20;
        if (size >= 9)
            return 25;
        return 0;
    }

    private void sortByPoints()
    {
        Collections.sort(patternList, new Comparator<RuzzlePattern>()
        {
            @Override
            public int compare(RuzzlePattern o1, RuzzlePattern o2)
            {
                return ComparisonChain.start().compare(o2.score,o1.score).result();
            }
        });
    }

    private void filterList()
    {
        HashSet<String> words = new HashSet<>();
        for (Iterator<RuzzlePattern> iterator = patternList.iterator(); iterator.hasNext(); )
        {
            RuzzlePattern pattern = iterator.next();
            if (words.contains(pattern.word) || (pattern.word.length() == 1))
            {
                iterator.remove();
            } else
            {
                words.add(pattern.word);
            }
        }
    }

    public void readWords(READ_STYLE readStyle) throws IOException
    {
        System.out.println("Reading ruzzle characters");
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String letter = br.readLine();
                matrix[i][j] = new Cell();
                matrix[i][j].character = letter.charAt(0);
                if(readStyle.equals(READ_STYLE.WITH_SCORE))
                {
                    matrix[i][j].characterPoint = Integer.parseInt(String.valueOf(letter.charAt(1)));
                    if (letter.length() == 2)
                        matrix[i][j].cellMultiplier = null;
                    else if (letter.charAt(2) == '1')
                        matrix[i][j].cellMultiplier = Cell.MULTIPLIER.DL;
                    else if (letter.charAt(2) == '2')
                        matrix[i][j].cellMultiplier = Cell.MULTIPLIER.TL;
                    else if (letter.charAt(2) == '3')
                        matrix[i][j].cellMultiplier = Cell.MULTIPLIER.DW;
                    else if (letter.charAt(2) == '4')
                        matrix[i][j].cellMultiplier = Cell.MULTIPLIER.TW;
                }
            }
        }
    }

    public void printMatrix()
    {
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                System.out.print(matrix[i][j].character + "\t");
            }
            System.out.println();
        }
    }

    private void dfs(final int i, final int j, boolean[][] visited, final String passedString, Vector<Pair> coorList)
    {

        int[] diffxy = {-1, 0, 1};
        visited[i][j] = true;
        String currentString = passedString + String.valueOf(matrix[i][j].character);
        coorList.add(new Pair(i, j));
        if (wordList.contains(currentString))
        {
            patternList.add(new RuzzlePattern(new Vector<Pair>(coorList), currentString));
        }

        for (int x = 0; x < diffxy.length; x++)
        {
            for (int y = 0; y < diffxy.length; y++)
            {
                if (x == 1 && y == 1)
                    continue;
                int newi = i + diffxy[x];
                int newj = j + diffxy[y];
                if (inbound(newi, newj) && !visited[newi][newj])
                {
                    dfs(newi, newj, visited, currentString, coorList);
                }

            }
        }
        visited[i][j] = false;
        coorList.remove(coorList.size() - 1);
    }

    private boolean inbound(int newi, int newj)
    {
        return newi >= 0 && newi < 4 && newj >= 0 && newj < 4;
    }

    public void loadDictionary(String fileName)
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileName)));
            List<String> lines = new ArrayList<>();
            String line = null;
            wordList = new HashSet<>();
            Pattern regex = Pattern.compile("[^a-z]");
            while ((line = bufferedReader.readLine()) != null)
            {
                Matcher matcher = regex.matcher(line);
                if (!matcher.find())
                {
                    wordList.add(line);
                }
            }
            bufferedReader.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

class Pair
{
    int x;
    int y;

    public Pair(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
}