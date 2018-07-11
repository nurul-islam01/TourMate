package com.example.nurulislam.tourmate;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nurulislam.tourmate.POJO.ExpenseList;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static com.example.nurulislam.tourmate.MainActivity.USERID;
import static com.example.nurulislam.tourmate.TourExpence.TAG;

public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ExpenseHolder> {
    private List<ExpenseList> expenseLists;
    private FirebaseDatabase rootref;
    private DatabaseReference useref;

    public ExpenseListAdapter(@NonNull List<ExpenseList> expenseLists) {
        this.expenseLists = expenseLists;
    }

    @NonNull
    @Override
    public ExpenseListAdapter.ExpenseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.expense_list_row,parent,false);

        return new ExpenseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseListAdapter.ExpenseHolder holder, int position) {
        try {
            holder.ex_row_commentTV.setText(expenseLists.get(position).getExpComment());
            holder.ex_list_amountTV.setText(String.valueOf(expenseLists.get(position).getAmount())+"TK");
        }catch (Exception e){
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return expenseLists.size();
    }

    public class ExpenseHolder extends RecyclerView.ViewHolder{

        TextView ex_row_commentTV,ex_list_amountTV;
        ImageView ex_row_edit_iconTV;

        public ExpenseHolder(final View itemView) {
            super(itemView);
            rootref = FirebaseDatabase.getInstance();
            useref = rootref.getReference().child(USERID).child("All_EVENTS").child(TourExpence.event_id).child("Expense").getRef();
            ex_row_commentTV = itemView.findViewById(R.id.ex_row_comment);
            ex_list_amountTV = itemView.findViewById(R.id.ex_list_amount);
            ex_row_edit_iconTV = itemView.findViewById(R.id.ex_row_edit_icon);
            ex_row_edit_iconTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    rootref = FirebaseDatabase.getInstance();
                    useref = rootref.getReference().child(USERID).child("All_EVENTS").child(TourExpence.event_id).child("Expense").child("ExpenseList").child(expenseLists.get(getAdapterPosition()).getExpenseId()).getRef();

                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    LayoutInflater inflater = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.add_expense_dialoge, null);

                    final EditText ex_dia_comment;
                    final EditText ex_dia_amount;
                    ex_dia_comment = view.findViewById(R.id.ex_dia_comment);
                    ex_dia_amount = view.findViewById(R.id.ex_dia_amount);

                    try {
                        ex_dia_comment.setText(expenseLists.get(getAdapterPosition()).getExpComment());
                        ex_dia_amount.setText(String.valueOf(expenseLists.get(getAdapterPosition()).getAmount()));
                    }catch (Exception e){
                        Toast.makeText(itemView.getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                    builder.setView(view).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                               String comment = ex_dia_comment.getText().toString();
                               double exp =  Double.parseDouble(String.valueOf(ex_dia_amount.getText()));
                               String ex_list_id = expenseLists.get(getAdapterPosition()).getExpenseId();

                               ExpenseList expenseList = new ExpenseList(comment,exp,ex_list_id);
                               useref.setValue(expenseList);
                            }catch (Exception e){
                                Log.d(TAG, "onClick: "+e.getMessage());
                            }




                        }
                    })
                            .setNegativeButton("Cancel",null)
                            .show();

                }
            });
        }
    }
}
