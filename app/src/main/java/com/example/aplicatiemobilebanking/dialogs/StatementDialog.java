package com.example.aplicatiemobilebanking.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.aplicatiemobilebanking.R;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;

import androidx.fragment.app.DialogFragment;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.User;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import es.voghdev.pdfviewpager.library.PDFViewPagerZoom;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;

public class StatementDialog extends DialogFragment {

    private BankAccount bankAccount;
    private User user;
    private ArrayList<Transaction> transactions;
    private PDFViewPagerZoom pdfView;
    private File pdfFile;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Bank Account Statement");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_statement, null);
        builder.setView(view);

        pdfView = view.findViewById(R.id.statementDiag_pdfView);

        bankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");
        user = (User) getArguments().getSerializable("USER");
        transactions = (ArrayList<Transaction>) getArguments().getSerializable("TRANSACTIONS");

        generateStatementPdf();

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveStatementPdf();
            }
        });
        builder.setNegativeButton("Close", null);

        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button printButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                printButton.setTextColor(Color.BLACK);
                Button closeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                closeButton.setTextColor(Color.BLACK);
            }
        });
        return dialog;
    }

    private void generateStatementPdf() {
        try {
            pdfFile = new File(requireContext().getExternalFilesDir(null), "statement.pdf");

            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdfDoc = new PdfDocument(writer);

            Document document = new Document(pdfDoc);

            PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            float titleFontSize = 18;

            Paragraph title = new Paragraph("Bank Account Statement")
                    .setFont(titleFont)
                    .setFontSize(titleFontSize)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Paragraph dateParagraph = new Paragraph(currentDate)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20f);
            document.add(dateParagraph);

            Table infoTable = new Table(2);
            infoTable.setWidth(UnitValue.createPercentValue(100));

            Paragraph userParagraph = new Paragraph()
                    .add(new Text("User Information\n").setBold().setFontSize(13f))
                    .add("Name: " + user.getFirstName() + " " + user.getLastName() + "\n")
                    .add("Address: " + user.getAddress() + "\n")
                    .add("Phone Number: " + user.getPhoneNumber() + "\n")
                    .add("Email: " + user.getEmail() + "\n\n")
                    .setMarginBottom(20f);
            Cell userCell = new Cell().add(userParagraph);
            infoTable.addCell(userCell);

            Paragraph accountParagraph = new Paragraph()
                    .add(new Text("Account Details\n").setBold().setFontSize(13f))
                    .add("IBAN: " + bankAccount.getIban() + "\n")
                    .add("SWIFT: " + bankAccount.getSwift() + "\n")
                    .add("Balance: " + bankAccount.getBalance() + " " + bankAccount.getCurrency() + "\n\n")
                    .setMarginBottom(20f);
            Cell accountCell = new Cell().add(accountParagraph);
            infoTable.addCell(accountCell);

            document.add(infoTable);

            Paragraph transactionsSubtitle = new Paragraph("Transactions")
                    .setFont(titleFont)
                    .setFontSize(14);
            document.add(transactionsSubtitle);

            Table transactionsTable = new Table(3);
            transactionsTable.setWidth(UnitValue.createPercentValue(100));

            Paragraph merchantHeader = new Paragraph(new Text("Merchant").setBold());
            Paragraph dateHeader = new Paragraph(new Text("Date").setBold());
            Paragraph amountHeader = new Paragraph(new Text("Amount").setBold());

            transactionsTable.addCell(new Cell().add(merchantHeader));
            transactionsTable.addCell(new Cell().add(dateHeader));
            transactionsTable.addCell(new Cell().add(amountHeader));

            for (Transaction transaction : transactions) {
                transactionsTable.addCell(new Cell().add(new Paragraph(transaction.getMerchant())));
                transactionsTable.addCell(new Cell().add(new Paragraph(transaction.getDate().toString())));
                transactionsTable.addCell(new Cell().add(new Paragraph(getString(R.string.RON,String.valueOf(transaction.getAmount())))));
            }

            document.add(transactionsTable);

            document.close();
            pdfDoc.close();
            writer.close();

            pdfView.setAdapter(new PDFPagerAdapter(requireContext(), pdfFile.getAbsolutePath()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveStatementPdf() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "statement_" + timeStamp + ".pdf";
            File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

            if (pdfFile.exists()) {
                boolean success = pdfFile.renameTo(destinationFile);
                if (success) {
                    Toast.makeText(requireContext(), "PDF saved successfully to /Downloads", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to save PDF", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "PDF file not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to save PDF", Toast.LENGTH_SHORT).show();
        }
    }


}
