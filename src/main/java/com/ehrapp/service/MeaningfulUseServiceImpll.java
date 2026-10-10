package com.ehrapp.service;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ehrapp.model.StateRecord;
import com.opencsv.CSVReader;

@Service
public class MeaningfulUseServiceImpll implements MeaningfulUseService {
	
	private final String dataUrl;

    public MeaningfulUseServiceImpll(@Value("${ehr.data-url}") String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public List<com.ehrapp.model.StateRecord> getMeaningfulUse2014() throws Exception {

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(dataUrl))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to download dataset: " + response.statusCode());
        }

        List<StateRecord> records = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new StringReader(response.body()))) {

            String[] header = reader.readNext();

            if (header == null) {
                throw new RuntimeException("CSV header is null.");
            }

            Map<String, Integer> index = new HashMap<>();

            for (int i = 0; i < header.length; i++) {
                index.put(header[i].trim().toLowerCase(), i);
            }

            Integer regionIndex = index.get("region");
            Integer periodIndex = index.get("period");
            Integer pctIndex = index.get("pct_hospitals_mu");

            if (regionIndex == null || periodIndex == null || pctIndex == null) {
                throw new RuntimeException("Required columns not found in CSV");
            }

            String[] row;

            while ((row = reader.readNext()) != null) {

                String period = row[periodIndex];

                if ("2014".equals(period)) {

                    String state = row[regionIndex];

                    // Exclude National record
                    if ("National".equalsIgnoreCase(state)) {
                        continue;
                    }

                    String pct = row[pctIndex];

                    if (pct != null && !pct.isEmpty()) {

                        records.add(
                            new StateRecord(
                                state,
                                Double.parseDouble(pct) * 100
                            )
                        );
                    }
                }
            }
        }

        return records.stream()
                .sorted(
                    Comparator.comparingDouble(
                        StateRecord::percentageOfHospitals
                    ).reversed()
                )
                .toList();
        
}
    public List<StateRecord> getMeaningfulUse(int year) throws Exception {

        if (year == 2014) {
            return getMeaningfulUse2014();
        }

        throw new RuntimeException("Data for year " + year + " not supported");
    }}
