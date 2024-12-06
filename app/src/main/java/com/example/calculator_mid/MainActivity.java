package com.example.calculator_mid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private String expression = "";
    TextView resultText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        resultText = findViewById(R.id.textView);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                expression = s.toString();
                // Replace "×" with "*" and "÷" with "/" for calculation purposes
                expression = expression.replace("×", "*").replace("÷", "/");
                autoCalculate();
            }
        });
    }

    public void onButtonClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        int cursorPosition = editText.getSelectionStart();

        switch (buttonText) {
            case "C":
                expression = "";
                editText.setText(expression);
                resultText.setText(expression);
                break;

            case "DEL":
                if (!expression.isEmpty() && cursorPosition > 0) {
                    // Delete character at the cursor position
                    expression = editText.getText().toString();
                    StringBuilder stringBuilder = new StringBuilder(expression);
                    stringBuilder.deleteCharAt(cursorPosition - 1);  // Delete the character before the cursor
                    expression = stringBuilder.toString();
                    editText.setText(expression);
                    // Move cursor back to the right position
                    editText.setSelection(cursorPosition - 1);
                }
                break;

            case "=":
                evaluateExpression();
                editText.setText(expression);
                editText.setSelection(editText.getText().length());
                break;

            default:
                expression = editText.getText().toString();
                // Insert the number or operator at the current cursor position
                StringBuilder stringBuilder = new StringBuilder(expression);
                stringBuilder.insert(cursorPosition, buttonText);  // Insert character at cursor position
                expression = stringBuilder.toString();

                editText.setText(expression);
                // Move cursor to the new position after the inserted character
                editText.setSelection(cursorPosition + buttonText.length());
                break;

        }
    }

    private void autoCalculate() {
        try {
            if (!expression.isEmpty() && containsOperator(expression)) {
                Expression exp = new ExpressionBuilder(expression).build();
                double result = exp.evaluate();
                resultText.setText(Double.toString(result));
                if (resultText.getText().toString().endsWith(".0")) {
                    resultText.setText(resultText.getText().toString().replace(".0", ""));
                }
            } else {
                // If only a number or incomplete expression is entered, clear the resultText
                resultText.setText("");
            }
        } catch (ArithmeticException | IllegalArgumentException e) {
            resultText.setText("");
        }
    }

    private void evaluateExpression() {
        try {
            if (!expression.isEmpty()) {
                // Replace "×" with "*" and "÷" with "/" for calculation purposes
                expression = expression.replace("×", "*").replace("÷", "/");
                Expression exp = new ExpressionBuilder(expression).build();
                double result = exp.evaluate();
                expression = Double.toString(result);
                if (expression.endsWith(".0")) {
                    expression = expression.substring(0, expression.length() - 2);  // Remove ".0"
                }
            }
        } catch (ArithmeticException | IllegalArgumentException e) {
            Toast.makeText(this, "Invalid Expression", Toast.LENGTH_SHORT).show();
            expression = "";
        }
    }

    // Helper method to check if the expression contains any operator
    private boolean containsOperator(String input) {
        return input.contains("+") || input.contains("-") || input.contains("*") || input.contains("/");
    }
}
