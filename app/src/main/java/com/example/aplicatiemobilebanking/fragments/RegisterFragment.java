package com.example.aplicatiemobilebanking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RegisterFragment extends Fragment {

    TextInputEditText tietFirstName, tietLastName, tietIdNumber, tietPhoneNumber, tietAddres, tietEmail, tietPassword;
    Button btCreateAccount, btBack;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        tietPhoneNumber = view.findViewById(R.id.registerFrag_tietPhone);
        tietAddres = view.findViewById(R.id.registerFrag_tietAddress);
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
                if (!isValidName(s.toString())) tietFirstName.setError("Invalid First Name");
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
                if (!isValidName(s.toString())) tietLastName.setError("Invalid Last Name");
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

        tietAddres.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValidAddres(s.toString())) tietAddres.setError("Invalid Addres");
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
                if (!isValidEmail(s.toString())) tietEmail.setError("Invalid Email Addres");
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
                if (!isValidName(tietLastName.getText().toString())) {
                    tietLastName.setError("Invalid First Name");
                    dataIsCorrect = false;
                }
                if (!isValidIdNumber(tietIdNumber.getText().toString())) {
                    tietIdNumber.setError("Invalid Personal ID Number");
                    dataIsCorrect = false;
                }
                if (!isValidPhoneNumber(tietPhoneNumber.getText().toString())) {
                    tietPhoneNumber.setError("Invalid Phone Number");
                    dataIsCorrect = false;
                }
                if (!isValidAddres(tietAddres.getText().toString())) {
                    tietAddres.setError("Invalid Addres");
                    dataIsCorrect = false;
                }
                if (!isValidEmail(tietEmail.getText().toString())) {
                    tietEmail.setError("Invalid Email Addres");
                    dataIsCorrect = false;
                }
                if (!isValidPassword(tietPassword.getText().toString())) {
                    tietPassword.setError("Password must contain:\n" +
                            "- 8 characters\n" +
                            "- 1 Uppercase/Lowercase letters \n" +
                            "- 1 Special character");
                    dataIsCorrect = false;
                }

                if (dataIsCorrect) {
                    User user = new User();
                    user.setFirstName(tietFirstName.getText().toString());
                    user.setLastName(tietLastName.getText().toString());
                    user.setIdentificationNumber(tietIdNumber.getText().toString());
                    user.setAddress(tietAddres.getText().toString());
                    user.setPhoneNumber(tietPhoneNumber.getText().toString());
                    user.setEmail(tietEmail.getText().toString());
                    user.setPassword(tietPassword.getText().toString());
                    addUserToDatabase(user);

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


    private void addUserToDatabase(User user) {
        String idNumber = user.getIdentificationNumber();
        String email = user.getEmail();

        // Check if user with same ID Number already exists
        db.collection("users").document(idNumber).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // User with same ID Number already exists
                            tietIdNumber.setError("Personal ID Number already exists in database");
                        } else {
                            // No user with same ID Number exists
                            // Check if user with same email already exists
                            db.collection("users")
                                    .whereEqualTo("email", email).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                // User with same email already exists
                                                tietEmail.setError("Email already exists");
                                            } else {
                                                // No user with same email exists
                                                // Add user to database
                                                db.collection("users").document(idNumber)
                                                        .set(user)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                BankAccount bankAccount = assignBankAccount(user);
                                                                assignCreditCard(user, bankAccount);
                                                                Toast.makeText(getContext(), "Account created", Toast.LENGTH_SHORT).show();
                                                                openLoginFragment();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Handle error adding user
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle error querying database for user with same email
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle error querying database for user with same ID Number
                    }
                });
    }


    private BankAccount assignBankAccount(User user) {
        String IBAN = generateIban();
        String SWIFT = "BTRLRO22";

        BankAccount bankAccount = new BankAccount(IBAN, SWIFT, 500.00f, "RON",
                user.getIdentificationNumber());

        db.collection("bankAccounts").document(IBAN)
                .set(bankAccount)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Bank account added successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle error adding bank account
                    }
                });
        return bankAccount;
    }

    private void assignCreditCard(User user, BankAccount bankAccount) {

        CreditCard creditCard = new CreditCard(generateCreditCardNumber(),
                user.getFullName(), dateGenerator(), CVVGenerator(), 0, bankAccount.getIban());

        db.collection("creditCards").document(creditCard.getCardNumber())
                .set(creditCard)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Credit card added successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle error adding credit card
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
        return name.length() >= 2 && name.matches("[a-zA-Z]+");
    }

    public boolean isValidIdNumber(String idNumber) {
        return idNumber.length() == 13 && idNumber.matches("[0-9]+");
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.length() >= 6 && phoneNumber.matches("[0-9]+");
    }

    public boolean isValidAddres(String Addres) {
        return Addres.length() >= 10;
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

    public static String generateIban() {
        String countryCode = "RO";
        String bankCode = "BTRL";
        String currencyCode = "RON";
        String accountNumber = new Random().ints(0,10)
                .limit(13)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining());

        String iban = countryCode + "00" + bankCode + currencyCode + accountNumber;

        int remainder = iban.chars().reduce(0,
                (r, c) -> (r * 10 + Character.getNumericValue(c)) % 97);
        String checkDigitsStr = String.format("%02d", 98 - remainder);

        return countryCode + checkDigitsStr + bankCode + currencyCode + accountNumber;
    }


    public static String generateCreditCardNumber() {
        Random random = new Random();
        String ccNumber = random.ints(11, 0, 10)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining());
        ccNumber = "4140" + ccNumber + getCheckDigit("4140" + ccNumber);
        return ccNumber;
    }

    private static int getCheckDigit(String ccNumber) {
        int sum = IntStream.range(0, ccNumber.length())
                .map(i -> ccNumber.charAt(i) - '0')
                .map(i -> ccNumber.length() % 2 == i % 2 ? (2 * i) % 9 : i)
                .sum();
        return (10 - sum % 10) % 10;
    }

    public int CVVGenerator() {
        Random rand = new Random();
        int firstDigit = 3;
        int secondDigit = rand.nextInt(5) + 5; // Generate a random number between 5 and 9
        int thirdDigit = rand.nextInt(10); // Generate a random number between 0 and 9
        return firstDigit * 100 + secondDigit * 10 + thirdDigit;
    }

    private Date dateGenerator() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 3);
        Date date = calendar.getTime();
        return date;
    }

}