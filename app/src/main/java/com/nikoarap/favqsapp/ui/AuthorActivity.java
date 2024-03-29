package com.nikoarap.favqsapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.nikoarap.favqsapp.R;
import com.nikoarap.favqsapp.adapters.QuotesAdapter;
import com.nikoarap.favqsapp.api.FetchJSONDataAPI;
import com.nikoarap.favqsapp.api.RetrofitRequestClass;
import com.nikoarap.favqsapp.models.QuoteModel;
import com.nikoarap.favqsapp.models.Quotes;
import com.nikoarap.favqsapp.utils.VerticalSpacingDecorator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

public class AuthorActivity extends AppCompatActivity implements QuotesAdapter.OnQuoteListener{

    public static final String TAG = "AuthorActivity";

    private Quotes[] quotes;
    private String quoteId;
    private String quoteBody;
    private String quoteAuthor;
    private String quoteAuthorPerma;
    private String quoteUpvotes;
    private String quoteDownvotes;
    private String[] quoteTags;
    private String quoteFavCount;
    private String quoteAuthor_2;

    private RecyclerView recView;
    public ArrayList<Quotes> quoteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_layout);
        recView = findViewById(R.id.quotesRecyclerView);
        ButterKnife.bind(this);

        Intent i = getIntent();
        quoteId = i.getStringExtra("quoteId");
        quoteBody = i.getStringExtra("quoteBody");
        quoteAuthor = i.getStringExtra("quoteAuthor");
        quoteAuthorPerma = i.getStringExtra("quoteAuthorPerma");
        quoteUpvotes = i.getStringExtra("quoteUpvotes");
        quoteDownvotes = i.getStringExtra("quoteDownvotes");
        quoteTags = i.getStringArrayExtra("guoteTags");
        quoteFavCount = i.getStringExtra("quoteFavCount");

        quoteAuthor_2 = quoteAuthor.replaceAll("\\s+","+");

        fetchQuoteList();

    }

    private void fetchQuoteList() {
        FetchJSONDataAPI service = RetrofitRequestClass.fetchApi();



        Call<QuoteModel> call = service.getQuotesByAuthor(quoteAuthor_2,"&","author");
        call.enqueue(new Callback<QuoteModel>() {
            @Override
            public void onResponse(Call<QuoteModel> call, Response<QuoteModel> response) {
                if (response.body() != null) {
                    String str = response.body().getQuotes().toString();
                    Log.i(TAG, "onResponse: "+str);
                    QuoteModel quoteModel = response.body();
                    quoteModel.setQuotes(response.body().getQuotes());
                    quotes = quoteModel.getQuotes();
                    for(Quotes quote: quotes){
                        populateRecyclerView(quotes);
                        quoteList.add(quote);

                    }

                }
            }

            @Override
            public void onFailure(Call<QuoteModel> call, @NotNull Throwable t) {
                Toast.makeText(AuthorActivity.this, "error" ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateRecyclerView(Quotes[] quoteList) {
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recView.setLayoutManager(linearLayoutManager);
        QuotesAdapter recAdapter = new QuotesAdapter(this, quoteList, this);
        VerticalSpacingDecorator itemDecorator = new VerticalSpacingDecorator(1);
        recView.addItemDecoration(itemDecorator);
        recView.setAdapter(recAdapter);
        recAdapter.notifyDataSetChanged();
        recView.scheduleLayoutAnimation();
    }

    @Override
    public void onQuoteClick(int position) {
        Intent i = new Intent(AuthorActivity.this, QuoteActivity.class);
        i.putExtra("quoteId", quoteList.get(position).getId());
        i.putExtra("quoteBody", quoteList.get(position).getBody());
        i.putExtra("quoteAuthor", quoteList.get(position).getAuthor());
        i.putExtra("quoteAuthorPerma", quoteList.get(position).getAuthor_permalink());
        i.putExtra("quoteUpvotes", quoteList.get(position).getUpvotes_count());
        i.putExtra("quoteDownvotes", quoteList.get(position).getDownvotes_count());
        i.putExtra("guoteTags", quoteList.get(position).getTags());
        i.putExtra("quoteFavCount", quoteList.get(position).getFavorites_count());
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AuthorActivity.this,UserMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
