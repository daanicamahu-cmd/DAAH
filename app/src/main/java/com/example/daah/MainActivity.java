package com.example.daah;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;

    private double firstOperand = Double.NaN;
    private String pendingOperator = null;
    private boolean startNewNumber = true;
    private boolean justEvaluated = false;
    private String currentInput = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDisplay = findViewById(R.id.tvDisplay);
    }

    public void onDigit(View view) {
        String digit = ((Button) view).getText().toString();
        if (startNewNumber || justEvaluated) {
            currentInput = digit;
            startNewNumber = false;
            justEvaluated = false;
        } else if (currentInput.equals("0")) {
            currentInput = digit;
        } else {
            currentInput += digit;
        }
        updateDisplay();
    }

    public void onDot(View view) {
        if (startNewNumber || justEvaluated) {
            currentInput = "0.";
            startNewNumber = false;
            justEvaluated = false;
        } else if (!currentInput.contains(".")) {
            currentInput += ".";
        }
        updateDisplay();
    }

    public void onOperator(View view) {
        String operator = String.valueOf(view.getTag());
        double inputValue = parseDisplay();

        if (!Double.isNaN(firstOperand) && !startNewNumber) {
            firstOperand = calculate(firstOperand, inputValue, pendingOperator);
            currentInput = format(firstOperand);
        } else {
            firstOperand = inputValue;
        }

        pendingOperator = operator;
        startNewNumber = true;
        justEvaluated = false;
        updateDisplay();
    }

    public void onEquals(View view) {
        if (pendingOperator == null) {
            return;
        }
        double inputValue = parseDisplay();
        firstOperand = calculate(firstOperand, inputValue, pendingOperator);
        currentInput = format(firstOperand);
        firstOperand = Double.NaN;
        pendingOperator = null;
        startNewNumber = true;
        justEvaluated = true;
        updateDisplay();
    }

    public void onClear(View view) {
        firstOperand = Double.NaN;
        pendingOperator = null;
        startNewNumber = true;
        justEvaluated = false;
        currentInput = "0";
        updateDisplay();
    }

    public void onDelete(View view) {
        if (startNewNumber || justEvaluated) {
            return;
        }
        if (currentInput.length() > 1) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        } else {
            currentInput = "0";
            startNewNumber = true;
        }
        updateDisplay();
    }

    public void onPercent(View view) {
        double value = parseDisplay() / 100.0;
        currentInput = format(value);
        startNewNumber = false;
        justEvaluated = false;
        updateDisplay();
    }

    public void onSqrt(View view) {
        double value = parseDisplay();
        currentInput = value < 0 ? "Error" : format(Math.sqrt(value));
        startNewNumber = true;
        justEvaluated = false;
        updateDisplay();
    }

    public void onSquare(View view) {
        double value = parseDisplay();
        currentInput = format(value * value);
        startNewNumber = true;
        justEvaluated = false;
        updateDisplay();
    }

    public void onReciprocal(View view) {
        double value = parseDisplay();
        currentInput = value == 0 ? "Error" : format(1.0 / value);
        startNewNumber = true;
        justEvaluated = false;
        updateDisplay();
    }

    public void onToggleSign(View view) {
        if (currentInput.equals("Error") || currentInput.equals("0")) {
            return;
        }
        if (currentInput.startsWith("-")) {
            currentInput = currentInput.substring(1);
        } else {
            currentInput = "-" + currentInput;
        }
        updateDisplay();
    }

    private double parseDisplay() {
        try {
            return Double.parseDouble(currentInput);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double calculate(double a, double b, String operator) {
        if (operator == null) {
            return b;
        }
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) {
                    return Double.NaN;
                }
                return a / b;
            default:
                return b;
        }
    }

    private String format(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "Error";
        }
        DecimalFormat df;
        if (value == Math.rint(value)) {
            df = new DecimalFormat("#");
        } else {
            df = new DecimalFormat("#.##########");
        }
        return df.format(value);
    }

    private void updateDisplay() {
        tvDisplay.setText(currentInput);
    }
}
