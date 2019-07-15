package com.example.fivedicegame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fivedicegame.myDice.Five;
import com.example.fivedicegame.myDice.Four;
import com.example.fivedicegame.myDice.One;
import com.example.fivedicegame.myDice.Six;
import com.example.fivedicegame.myDice.Three;
import com.example.fivedicegame.myDice.Two;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@SuppressLint("Registered")
public class Game extends AppCompatActivity {

    String name1, name2, name;
    private boolean[] isLocked;
    private float dotWidth;
    private int[] oldResults, backResults;
    private int[] initialList;
    private ArrayList<Integer> disableSpinnerItemList1, disableSpinnerItemList2, disableSpinnerItemList;
    private int diceSize;
    TextView finalResult, totalNumber, posibilyAditional, playerName, score;
    RelativeLayout rl;
    private Button[] dices;
    private int roundCounter, round, playerIndex;
    RelativeLayout diceContainer, diceRowTwo;
    Spinner spinner;
    ArrayList<String> actualSpinnerListPlayer1, actualSpinnerListPlayer2;
    //Results player1, player2;
    int pos = 0;
    ArrayList<Integer> p1, p2;
    int additionalVal = 0;
    int scorePlayer1, scorePlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        name1 = getIntent().getStringExtra("name1");
        name2 = getIntent().getStringExtra("name2");
        finalResult = findViewById(R.id.resultTextView);
        totalNumber = findViewById(R.id.numberTotalRound);
        spinner = findViewById(R.id.spinner);
        score = findViewById(R.id.score);
        playerName = findViewById(R.id.name);
        playerName.setText(name1);
        //inicjalizujemy puste listy punktów
        initialList = new int[13];
        p1 = new ArrayList<>();
        p2 = new ArrayList<>();
        for (int i : initialList) {
            p1.add(i);
            p2.add(i);
        }
        disableSpinnerItemList = new ArrayList<>();
        disableSpinnerItemList1 = new ArrayList<>();
        disableSpinnerItemList2 = new ArrayList<>();
        actualSpinnerListPlayer1 = new ArrayList<>();
        actualSpinnerListPlayer2 = new ArrayList<>();
        actualSpinnerListPlayer1.addAll(Arrays.asList(getResources().getStringArray(R.array.tasks)));
        actualSpinnerListPlayer2.addAll(Arrays.asList(getResources().getStringArray(R.array.tasks)));
        posibilyAditional = findViewById(R.id.posiblyAdditional);
        rl = findViewById(R.id.points);

        //ustawienie rozstawienia oczek na kostce
        setEyes();

        dices = new Button[5];
        dices[0] = findViewById(R.id.button_dice_one);
        dices[1] = findViewById(R.id.button_dice_two);
        dices[2] = findViewById(R.id.button_dice_three);
        dices[3] = findViewById(R.id.button_dice_four);
        dices[4] = findViewById(R.id.button_dice_five);

        round = 1;
        playerIndex = 1;
        name = name1;

        for (Button dice : dices) {
            dice.setWidth(Math.round(diceSize));
            dice.setHeight(Math.round(diceSize));
        }

        isLocked = new boolean[5];
        for (int k = 0; k < isLocked.length; k++) {
            isLocked[k] = false;
        }

        for (Button dice : dices) {
            dice.setBackground(getDrawable(R.drawable.invisible_button));
        }

        oldResults = new int[]{1, 2, 3, 4, 5};
        backResults = new int[]{1, 2, 3, 4, 5};

        roundCounter = 0;

        diceContainer = findViewById(R.id.dice_frame);
        diceRowTwo = findViewById(R.id.dice_frame_second);

        final Button randomButton = findViewById(R.id.button);
        randomButton.setOnClickListener(v -> {
            spinner.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
            rl.setVisibility(View.INVISIBLE);
            if (allTrue(isLocked)) {
                for (Button dice : dices) {
                    dice.setBackground(getDrawable(R.drawable.dice_final));
                }
                finalResult.setVisibility(View.VISIBLE);
                randomButton.setText("Nastepny Gracz");
                roundCounter = 3;
                for (int k = 0; k < isLocked.length; k++) {
                    isLocked[k] = false;
                }
                initializeSpinner();
            } else {

                if (diceContainer.getChildCount() > 0) diceContainer.removeAllViews();
                if (diceRowTwo.getChildCount() > 0) diceRowTwo.removeAllViews();
                setDice(rollDice(5));

                if (roundCounter == 0) {
                    for (Button dice : dices) {
                        dice.setBackground(getDrawable(R.drawable.dice));
                    }
                }

                if (roundCounter == 2) {
                    for (Button dice : dices) {
                        dice.setBackground(getDrawable(R.drawable.dice_final));
                    }
                    finalResult.setVisibility(View.VISIBLE);
                    randomButton.setText("Nastepny gracz");
                    initializeSpinner();
                }

                if (roundCounter == 3) {
                    resetInterface();
                    finalResult.setVisibility(View.INVISIBLE);
                    randomButton.setText("Losuj");
                    setMyResult();

                } else {
                    roundCounter++;
                }
                TextView roundCounterTextView = findViewById(R.id.roundTextView);
                roundCounterTextView.setText(String.valueOf(roundCounter));
                flashResult();
            }

            scorePlayer1= p1.stream().mapToInt(Integer::intValue).sum();
            scorePlayer2= p2.stream().mapToInt(Integer::intValue).sum();
            score.setText(""+ scorePlayer1 + " vs "+ scorePlayer2);
        });

        //wybieranie kostek ktore niepodlegaja zmianie
        for (int j = 0; j < dices.length; j++) {
            final int finalJ = j;
            dices[j].setOnClickListener(v -> {
                if (roundCounter == 0) {
                } else if (roundCounter != 3) {
                    setLock(finalJ, v);
                }
            });
        }
    }

    private void setEyes() {
        DisplayMetrics display = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(display);
        setSizes(display.widthPixels / 40, Math.round(display.widthPixels / 4));
    }

    public void setSizes(float dotWidth, int diceSize) {
        this.dotWidth = dotWidth;
        this.diceSize = diceSize;

    }

    public static boolean allTrue(boolean[] values) {
        for (boolean value : values) {
            if (!value)
                return false;
        }
        return true;
    }

    public void setDice(int[] results) {

        RelativeLayout diceContainer = findViewById(R.id.dice_frame);
        RelativeLayout diceRowTwo = findViewById(R.id.dice_frame_second);

        for (int i = 0; i < 3; i++) {
            diceContainer.addView(displayResults(results[i], dices[i]));
        }

        for (int k = 3; k < dices.length; k++) {
            diceRowTwo.addView(displayResults(results[k], dices[k]));
        }

    }

    public View displayResults(int result, Button button) {

        View resultView = new View(getApplicationContext());

        switch (result) {
            case 1:
                resultView = new One(getApplicationContext(), button, dotWidth);
                break;
            case 2:
                resultView = new Two(getApplicationContext(), button, dotWidth);
                break;
            case 3:
                resultView = new Three(getApplicationContext(), button, dotWidth);
                break;
            case 4:
                resultView = new Four(getApplicationContext(), button, dotWidth);
                break;
            case 5:
                resultView = new Five(getApplicationContext(), button, dotWidth);
                break;
            case 6:
                resultView = new Six(getApplicationContext(), button, dotWidth);
                break;
            default:
                break;
        }

        return resultView;
    }

    public int[] rollDice(int poolSize) {
        int[] dice = new int[poolSize];

        for (int i = 0; i < dice.length; i++) {
            if (isLocked[i]) {
                dice[i] = oldResults[i];
            } else {
                SecureRandom random = new SecureRandom();
                byte[] bytes = new byte[6];
                random.nextBytes(bytes);
                dice[i] = random.nextInt(6) + 1;
                oldResults[i] = dice[i];
            }
        }
        System.arraycopy(dice, 0, backResults, 0, 5);
        return dice;
    }

    public void resetInterface() {
        roundCounter = 0;
        TextView roundCounterTextView = findViewById(R.id.roundTextView);
        roundCounterTextView.setText(String.valueOf(roundCounter));

        for (int k = 0; k < isLocked.length; k++) {
            isLocked[k] = false;
        }

        for (Button dice : dices) {
            dice.setBackground(getDrawable(R.drawable.dice));
        }

        RelativeLayout diceContainer = findViewById(R.id.dice_frame);
        RelativeLayout diceRowTwo = findViewById(R.id.dice_frame_second);

        if (diceContainer.getChildCount() > 0) diceContainer.removeAllViews();
        if (diceRowTwo.getChildCount() > 0) diceRowTwo.removeAllViews();

        for (Button dice : dices) {
            dice.setBackground(getDrawable(R.drawable.invisible_button));
        }
    }

    public void flashResult() {

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        animation.setStartOffset(20);
        animation.setRepeatMode(Animation.REVERSE);

        TextView finalResult = findViewById(R.id.resultTextView);

        for (int i = 0; i < dices.length; i++) {
            if (!isLocked[i])
                dices[i].startAnimation(animation);
        }

        if (roundCounter == 3) {
            finalResult.startAnimation(animation);
        }

    }

    public void setLock(int finalJ, View button) {
        boolean locked = isLocked[finalJ];
        if (!locked) {
            button.setBackground(getDrawable(R.drawable.dice_locked));
        } else {
            button.setBackground(getDrawable(R.drawable.dice));
        }
        isLocked[finalJ] = !isLocked[finalJ];
    }

    private void setMyResult() {
        if (playerIndex == 1) {
            disableSpinnerItemList1.add(pos);
            //actualSpinnerListPlayer1.remove(pos);
            playerIndex = 2;
            setPlayer(p1);
            playerName.setText(name2);
        } else {
            //actualSpinnerListPlayer2.remove(pos);
            disableSpinnerItemList2.add(pos);
            playerIndex = 1;
            setPlayer(p2);
            setPlayerTwo();
            playerName.setText(name1);
        }
    }

    private void initializeSpinner() {
        spinner.setVisibility(View.VISIBLE);
        ArrayList<String> actualSpinnerList;
        if (playerIndex == 1) {
            actualSpinnerList = actualSpinnerListPlayer1;
            disableSpinnerItemList = disableSpinnerItemList1;
        } else {
            actualSpinnerList = actualSpinnerListPlayer2;
            disableSpinnerItemList = disableSpinnerItemList2;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, actualSpinnerList);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rl.setVisibility(View.VISIBLE);
                switch (position) {
                    case 0:
                        additionalVal = firstSix(position + 1);
                        posibilyAditional.setText("" + additionalVal);
                        pos = position;
                        break;

                    case 1:
                        additionalVal = firstSix(position + 1);
                        posibilyAditional.setText("" + additionalVal);
                        pos = position;
                        break;
                    case 2:
                        additionalVal = firstSix(position + 1);
                        posibilyAditional.setText("" + additionalVal);
                        pos = position;
                        break;
                    case 3:
                        additionalVal = firstSix(position + 1);
                        posibilyAditional.setText("" + additionalVal);
                        pos = position;
                        break;
                    case 4:
                        additionalVal = firstSix(position + 1);
                        posibilyAditional.setText("" + additionalVal);
                        pos = position;
                        break;
                    case 5:
                        additionalVal = firstSix(position + 1);
                        posibilyAditional.setText("" + additionalVal);
                        pos = position;
                        break;
                    case 6:
                        additionalVal = same3();
                        posibilyAditional.setText("" + additionalVal);
                        pos = position;
                        break;
                    case 7:
                        additionalVal = same4();
                        posibilyAditional.setText("" + additionalVal);
                        pos = position;
                        break;
                    case 8:
                        additionalVal = ful();
                        posibilyAditional.setText("" + additionalVal);
                        pos = position;
                        break;
                    case 9:
                        additionalVal = mStrit();
                        posibilyAditional.setText("" + additionalVal);
                        pos = position;
                        break;
                    case 10:
                        additionalVal = dStrit();
                        posibilyAditional.setText("" + additionalVal);
                        pos = position;
                        break;
                    case 11:
                        additionalVal = general();
                        posibilyAditional.setText("" + additionalVal);
                        pos = position;
                        break;
                    case 12:
                        additionalVal = chance();
                        posibilyAditional.setText("" + additionalVal);
                        pos = position;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //zamiana wartości
    private void setPlayer(ArrayList<Integer> playerList) {
        playerList.set(pos, playerList.get(pos)+additionalVal);
    }

    private void setPlayerTwo() {
        if (round == 13) {
            //używam lambdy z javy 8 by dodać wszystkie elementy
            scorePlayer1= p1.stream().mapToInt(Integer::intValue).sum();
            scorePlayer2= p2.stream().mapToInt(Integer::intValue).sum();

            //premia
            int premiaP1 = 0;
            int premiaP2 = 0;
            for(int i=0; i<6; i++){
                premiaP1 += p1.get(i);
                premiaP2 += p2.get(i);
            }
            String winner = "";
            if(premiaP1 >= 63)
                scorePlayer1 += 35;
            if(premiaP2 >= 63)
                scorePlayer2 += 35;

            if(scorePlayer1>scorePlayer2){
                winner = name1;
            }else if(scorePlayer1<scorePlayer2){
                winner = name2;
            }else{
                winner = "draw";
            }
            Intent intent = new Intent();
            intent.putExtra("winner", winner);
            intent.putExtra("p1", scorePlayer1);
            intent.putExtra("p2", scorePlayer2);
            setResult(RESULT_OK, intent);
            finish();

        } else {
            //zwiększ runde
            round++;
            totalNumber.setText("" + round);
        }
    }

    private int firstSix(int search) {
        int res = 0;
        for (int i = 0; i < 5; i++) {
            if (backResults[i] == search)
                res += search;
        }
        return res;
    }

    private int same3() {
        ArrayList<Integer> intList = new ArrayList<>();
        for (int i : backResults) {
            intList.add(i);
        }
        Map<Integer, Integer> map = new LinkedHashMap<>();
        for (Integer i : intList) {
            Integer retrievedValue = map.get(i);
            if (null == retrievedValue) {
                map.put(i, 1);
            }
            else {
                map.put(i, retrievedValue + 1);
            }
        }
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int val = entry.getValue();
            if(val > 2) {
                return chance();
            }
        }
        return 0;
    }

    private int same4() {
        ArrayList<Integer> intList = new ArrayList<>();
        for (int i : backResults) {
            intList.add(i);
        }
        Map<Integer, Integer> map = new LinkedHashMap<>();
        for (Integer i : intList) {
            Integer retrievedValue = map.get(i);
            if (null == retrievedValue) {
                map.put(i, 1);
            }
            else {
                map.put(i, retrievedValue + 1);
            }
        }
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int val = entry.getValue();
            if(val > 3) {
                return chance();
            }
        }
        return 0;
    }

    private int ful() {
        ArrayList<Integer> intList = new ArrayList<>();
        for (int i : backResults) {
            intList.add(i);
        }
        Map<Integer, Integer> map = new LinkedHashMap<>();
        for (Integer i : intList) {
            Integer retrievedValue = map.get(i);
            if (null == retrievedValue) {
                map.put(i, 1);
            }
            else {
                map.put(i, retrievedValue + 1);
            }
        }
        int isTwo = 0;
        int isThree = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int val = entry.getValue();
            if(val == 2)
                isTwo = 1;
            else if(val == 3)
                isThree = 1;
        }

        if(isTwo == 1 && isThree == 1)
            return 25;
        else
            return 0;
    }

    private int mStrit(){
        Arrays.sort(backResults);
        ArrayList<Integer> intList = new ArrayList<>();
        for (int i : backResults) {
            intList.add(i);
        }
        Set<Integer> set = new HashSet<>(intList);
        intList.clear();
        intList.addAll(set);
        int isOne = 0;
        int isTwo = 0;
        int isFive = 0;
        int isSix = 0;
        for (int i = 0; i<intList.size(); i++) {
            if(intList.get(i) == 1)
                isOne=1;
            else if(intList.get(i) == 2)
                isTwo = 1;
            else if(intList.get(i) == 5)
                isFive = 1;
            else if(intList.get(i) == 6)
                isSix = 1;
        }
        if(intList.size() == 4){
            if(isOne == 1 && isFive == 0)
                return 30;
            else if(isOne == 0 && isSix ==0)
                return 30;
            else if(isOne == 0 && isTwo == 0)
                return 30;
            else
                return 0;
        }else if(intList.size() == 5){
            if(isOne != isSix)
                return 30;
        }
        return 0;
    }

    private int dStrit(){
        Arrays.sort(backResults);
        ArrayList<Integer> intList = new ArrayList<>();
        for (int i : backResults) {
            intList.add(i);
        }
        Set<Integer> set = new HashSet<>(intList);
        intList.clear();
        intList.addAll(set);
        int isOne = 0;
        int isSix = 0;
        if(intList.size() == 5){
            for (int i = 0; i<intList.size(); i++) {
                if(intList.get(i) == 1)
                    isOne=1;
                else if(intList.get(i) == 6)
                    isSix = 1;
            }
            if(isOne != isSix)
                return 40;
            else
                return 0;
        }
        return 0;
    }

    private int general(){
        ArrayList<Integer> intList = new ArrayList<>();
        for (int i : backResults) {
            intList.add(i);
        }
        Set<Integer> set = new HashSet<>(intList);
        intList.clear();
        intList.addAll(set);
        if(intList.size() == 1)
            return 50;
        return 0;
    }

    private int chance(){
        int res = 0;
        for (int i = 0; i < 5; i++) {
            res += backResults[i];
        }
        return res;
    }
}