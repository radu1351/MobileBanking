package com.example.aplicatiemobilebanking;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

    TextInputEditText tietFirstName, tietLastName, tietIdNumber, tietPhoneNumber, tietAdress, tietEmail, tietPassword;
    TextView tvDateOfBirth;
    Button btCreateAccount, btBack;

    public RegisterFragment() {
        // Required empty public constructor
    }


    public static RegisterFragment getInstance() {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        tietFirstName = view.findViewById(R.id.registerFrag_tietFirstName);
        tietLastName = view.findViewById(R.id.registerFrag_tietLastName);
        tietIdNumber = view.findViewById(R.id.registerFrag_tietIdentificationNumber);
        tvDateOfBirth = view.findViewById(R.id.registerFrag_tvDateOfBirth);
        tietPhoneNumber = view.findViewById(R.id.registerFrag_tietPhone);
        tietAdress = view.findViewById(R.id.registerFrag_tietAdress);
        tietEmail = view.findViewById(R.id.registerFrag_tietEmail);
        tietPassword = view.findViewById(R.id.registerFrag_tietPass);
        btCreateAccount = view.findViewById(R.id.registerFrag_btCreate);
        btBack = view.findViewById(R.id.registerFrag_btBack);

        tietFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            if(!isValidName(s.toString())) tietFirstName.setError("Invalid First Name");
            }
        });

        tietLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!isValidName(s.toString())) tietLastName.setError("Invalid Last Name");
            }
        });

        tietIdNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Timer...
                if (!isValidIdNumber(s.toString()))
                    tietIdNumber.setError("Invalid Personal ID Number");
            }
        });

        tietPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Timer...
                if (!isValidPhoneNumber(s.toString()))
                    tietPhoneNumber.setError("Invalid Phone Number");
            }
        });

        tietAdress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            if(!isValidAdress(s.toString())) tietAdress.setError("Invalid Adress");
            }
        });

        tietEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Timer...
                if (!isValidEmail(s.toString())) tietEmail.setError("Invalid Email Adress");
            }
        });

        tietPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValidPassword(s.toString()))
                    tietPassword.setError("Password must contain:\n" +
                            "- 8 characters\n" +
                            "- 1 Uppercase/Lowercase letters \n" +
                            "- 1 Special character");
            }
        });

        btCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean dataIsCorrect = true;

                if (!isValidName(tietFirstName.getText().toString())) {
                    tietFirstName.setError("Invalid First Name");
                    dataIsCorrect = false;
                }
                if(!isValidName(tietLastName.getText().toString())) {
                    tietLastName.setError("Invalid First Name");
                    dataIsCorrect = false;
                }
                if(!isValidIdNumber(tietIdNumber.getText().toString())) {
                    tietIdNumber.setError("Invalid Personal ID Number");
                    dataIsCorrect = false;
                }
                if(!isValidPhoneNumber(tietPhoneNumber.getText().toString())) {
                    tietPhoneNumber.setError("Invalid Phone Number");
                    dataIsCorrect = false;
                }
                if(!isValidAdress(tietAdress.getText().toString())) {
                    tietAdress.setError("Invalid Adress");
                    dataIsCorrect = false;
                }
                if(!isValidEmail(tietEmail.getText().toString())) {
                    tietEmail.setError("Invalid Email Adress");
                    dataIsCorrect = false;
                }
                if(!isValidPassword(tietPassword.getText().toString())) {
                    tietPassword.setError("Password must contain:\n" +
                            "- 8 characters\n" +
                            "- 1 Uppercase/Lowercase letters \n" +
                            "- 1 Special character");
                    dataIsCorrect = false;
                }

                if(dataIsCorrect){
                    Toast.makeText(getContext(),"Account has been succesfully created.",Toast.LENGTH_SHORT).show();
                    openLoginFragment();
                }
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginFragment();
            }
        });
    }


    public void openLoginFragment() {
        Fragment loginFragment = new LoginFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.loginAct_fl, loginFragment)
                .commit();
    }

    public boolean isValidName(String name) {
        return name.length()>=2 && name.matches("[a-zA-Z]+");
    }

    public boolean isValidIdNumber(String idNumber) {
        return idNumber.length() >= 8 && idNumber.matches("[0-9]+");
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.length() >= 6 && phoneNumber.matches("[0-9]+");
    }

    public boolean isValidAdress(String adress) {
        return adress.length() >= 10;
    }

    public boolean isValidEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = getString(R.string.email_pattern);
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isValidPassword(String password) {
        Pattern pattern;
        Matcher matcher;
        String PASSWORD_PATTERN = getString(R.string.password_pattern);
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

}