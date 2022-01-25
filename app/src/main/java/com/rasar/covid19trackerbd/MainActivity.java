package com.rasar.covid19trackerbd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mTodayTotal, mtotal, mActive, mTodayActive, mRecovered, mTodayRecovered, mDeath, MTodayDeath;

    String country;
    TextView mFiltter;
    Spinner mSpinner;
    String[] types= {"cases", "deaths", "recovered", "active"};
    private List<ModelClass> modelClassList;
    private List<ModelClass> modelClassList2;
    PieChart mPieChart;
    com.rasar.covid19trackerbd.Adapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        countryCodePicker = findViewById(R.id.ccpId);
        mTodayActive = findViewById(R.id.todayActiveId);
        mActive = findViewById(R.id.activeCaseId);
        mDeath = findViewById(R.id.totalDeathId);
        MTodayDeath = findViewById(R.id.todayDeathId);
        mRecovered = findViewById(R.id.recoveredCaseId);
        mTodayRecovered = findViewById(R.id.todayRecoveredId);
        mtotal = findViewById(R.id.totalCaseId);
        mTodayTotal = findViewById(R.id.todayTotalId);
        mPieChart = findViewById(R.id.pioeChartId);
        mSpinner = findViewById(R.id.spinnerId);
        mFiltter = findViewById(R.id.filtterId);
        recyclerView = findViewById(R.id.recyclerViewid);
        modelClassList = new ArrayList<>();
        modelClassList2 = new ArrayList<>();

        mSpinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(arrayAdapter);


        ApiUtilities.getApiInterface().getCountryData().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList2.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter = new Adapter(getApplicationContext(), modelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        country= countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country= countryCodePicker.getSelectedCountryName();
                fetchData();
            }
        });
        fetchData();
        
    }

    private void fetchData() {
        ApiUtilities.getApiInterface().getCountryData().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList.addAll(response.body());

                for (int i=0; i<modelClassList.size(); i++){
                    if (modelClassList.get(i).getCountry().equals(country)){
                        mActive.setText((modelClassList.get(i).getActive()));
                        MTodayDeath.setText((modelClassList.get(i).getTodayDeaths()));
                        mTodayRecovered.setText((modelClassList.get(i).getTodayRecovered()));
                        mTodayTotal.setText((modelClassList.get(i).getTodayCases()));
                        mtotal.setText((modelClassList.get(i).getCases()));
                        mDeath.setText((modelClassList.get(i).getDeaths()));
                        mRecovered.setText((modelClassList.get(i).getRecovered()));

                        int active, total, recovered, death;

                        active = Integer.parseInt(modelClassList.get(i).getActive());
                        total = Integer.parseInt(modelClassList.get(i).getCases());
                        recovered = Integer.parseInt(modelClassList.get(i).getRecovered());
                        death = Integer.parseInt(modelClassList.get(i).getDeaths());
                        
                        updategraph(active, total, recovered, death);


                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });
    }

    private void updategraph(int active, int total, int recovered, int death) {
        mPieChart.clearChart();
        mPieChart.addPieSlice(new PieModel("Total", total, Color.parseColor("#FFB701")));
        mPieChart.addPieSlice(new PieModel("Active", active, Color.parseColor("#FF4caf50")));
        mPieChart.addPieSlice(new PieModel("Recovered", recovered, Color.parseColor("#38ACCD")));
        mPieChart.addPieSlice(new PieModel("Deaths", death, Color.parseColor("#F55c47")));
        mPieChart.startAnimation();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = types[i];
        mFiltter.setText(item);
        adapter.filter(item);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}