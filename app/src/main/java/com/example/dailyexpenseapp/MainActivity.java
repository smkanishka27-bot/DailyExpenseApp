package com.example.dailyexpenseapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // ✅ Change these details if your teacher asks
    private static final String ACCOUNTANT_PHONE = "9876543210";
    private static final String ACCOUNTANT_EMAIL = "accountant@example.com";

    private EditText etAmount, etDesc;
    private Spinner spCategory;
    private RadioGroup rgPayMode;
    private ListView lvExpenses;

    private ArrayList<String> expenseList;
    private ArrayAdapter<String> expenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Daily Expense App - REGNO"); // ✅ Replace REGNO with your register number
        setContentView(R.layout.activity_main);

        etAmount = findViewById(R.id.etAmount);
        etDesc = findViewById(R.id.etDesc);
        spCategory = findViewById(R.id.spCategory);
        rgPayMode = findViewById(R.id.rgPayMode);
        lvExpenses = findViewById(R.id.lvExpenses);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnCall = findViewById(R.id.btnCall);
        Button btnSms = findViewById(R.id.btnSms);
        Button btnEmail = findViewById(R.id.btnEmail);

        // Spinner setup
        ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.expense_categories,
                android.R.layout.simple_spinner_item
        );
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(catAdapter);

        // ListView setup
        expenseList = new ArrayList<>();
        expenseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenseList);
        lvExpenses.setAdapter(expenseAdapter);

        btnAdd.setOnClickListener(v -> addExpense());

        btnCall.setOnClickListener(v -> showConfirmDialog(
                "Call Accountant",
                "Do you want to call the accountant?",
                this::callAccountant
        ));

        btnSms.setOnClickListener(v -> showConfirmDialog(
                "Send SMS",
                "Do you want to send SMS to the accountant?",
                this::smsAccountant
        ));

        btnEmail.setOnClickListener(v -> emailAccountant());
    }

    private void addExpense() {
        String amount = etAmount.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();

        int checkedId = rgPayMode.getCheckedRadioButtonId();
        String payMode = "";
        if (checkedId != -1) {
            RadioButton rb = findViewById(checkedId);
            payMode = rb.getText().toString();
        }

        if (amount.isEmpty()) {
            etAmount.setError("Enter amount");
            etAmount.requestFocus();
            return;
        }
        if (desc.isEmpty()) {
            etDesc.setError("Enter description");
            etDesc.requestFocus();
            return;
        }
        if (checkedId == -1) {
            Toast.makeText(this, "Select payment mode", Toast.LENGTH_SHORT).show();
            return;
        }

        String record = "₹" + amount + " | " + category + " | " + payMode + " | " + desc;
        expenseList.add(record);
        expenseAdapter.notifyDataSetChanged();

        etAmount.setText("");
        etDesc.setText("");
        rgPayMode.clearCheck();
        etAmount.requestFocus();
    }

    private void showConfirmDialog(String title, String message, Runnable onYes) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (d, w) -> onYes.run())
                .setNegativeButton("No", (d, w) -> d.dismiss())
                .show();
    }

    private void callAccountant() {
        Intent dial = new Intent(Intent.ACTION_DIAL);
        dial.setData(Uri.parse("tel:" + ACCOUNTANT_PHONE));
        startActivity(dial);
    }

    private void smsAccountant() {
        Intent sms = new Intent(Intent.ACTION_SENDTO);
        sms.setData(Uri.parse("smsto:" + ACCOUNTANT_PHONE));
        sms.putExtra("sms_body", "Hello, please review my daily expenses.");
        startActivity(sms);
    }

    private void emailAccountant() {
        Intent email = new Intent(Intent.ACTION_SENDTO);
        email.setData(Uri.parse("mailto:" + ACCOUNTANT_EMAIL));
        email.putExtra(Intent.EXTRA_SUBJECT, "Daily Expense Details");
        email.putExtra(Intent.EXTRA_TEXT, "Hello,\nPlease find my daily expense details.\nThank you.");
        startActivity(Intent.createChooser(email, "Choose Email App"));
    }
}