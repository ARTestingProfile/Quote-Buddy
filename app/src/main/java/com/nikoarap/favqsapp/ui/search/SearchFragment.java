package com.nikoarap.favqsapp.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.nikoarap.favqsapp.R;
import com.nikoarap.favqsapp.adapters.QuotesAdapter;
import com.nikoarap.favqsapp.api.FetchJSONDataAPI;
import com.nikoarap.favqsapp.api.RetrofitRequestClass;
import com.nikoarap.favqsapp.models.QuoteModel;
import com.nikoarap.favqsapp.models.Quotes;
import com.nikoarap.favqsapp.ui.QuoteActivity;
import com.nikoarap.favqsapp.ui.UserMenuActivity;
import com.nikoarap.favqsapp.utils.VerticalSpacingDecorator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements QuotesAdapter.OnQuoteListener {

    private Quotes[] quotes;

    private RecyclerView recView;
    public ArrayList<Quotes> quoteList = new ArrayList<>();

    private SearchView searchView;

    private SearchViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        recView = root.findViewById(R.id.quotesRecyclerView);
        searchView = root.findViewById(R.id.searchView);

        initSearchView();


        return root;
    }

    private void initSearchView(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                // Search the database for a recipe
                fetchQuoteList(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                // Wait for the user to submit the search. So do nothing here.

                return false;
            }
        });
    }

    private void fetchQuoteList(String query) {
        FetchJSONDataAPI service = RetrofitRequestClass.fetchApi();
        Call<QuoteModel> call = service.getQuotesByWord(query);
        call.enqueue(new Callback<QuoteModel>() {
            @Override
            public void onResponse(Call<QuoteModel> call, Response<QuoteModel> response) {
                if (response.body() != null) {
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
                Toast.makeText(getActivity(), "error" ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateRecyclerView(Quotes[] quoteList) {
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(linearLayoutManager);
        QuotesAdapter recAdapter = new QuotesAdapter(getActivity(), quoteList, this);
        VerticalSpacingDecorator itemDecorator = new VerticalSpacingDecorator(1);
        recView.addItemDecoration(itemDecorator);
        recView.setAdapter(recAdapter);
        recAdapter.notifyDataSetChanged();
        recView.scheduleLayoutAnimation();
    }

    @Override
    public void onQuoteClick(int position) {
        Intent i = new Intent(getActivity(), QuoteActivity.class);
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



}