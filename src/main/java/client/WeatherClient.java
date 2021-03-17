package client;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.weather.Day;
import com.weather.Forecast;
import com.weather.Forecaster;
import com.weather.Region;

public class WeatherClient {
    private static final int MAX_CACHE_LIMIT = 2;

    final Map<String, CustomForecast> cachedForecasts;
    final Forecaster forecaster;


    public WeatherClient(final Forecaster forecaster) {
        this.cachedForecasts = new LinkedHashMap<>();
        this.forecaster = forecaster;
    }

    public CustomForecast getForecast(final Region region, final Day day) {
        final String key = region.toString() + "-" + day.toString();

        final List<Map.Entry> entriesOlderThanAnHour =
            this.cachedForecasts.entrySet().stream().filter(stringCustomForecastEntry ->
            stringCustomForecastEntry.getValue().getTimeAdded().before(new Date(new Date().toInstant().minusSeconds(360L).getEpochSecond()))
        ).collect(Collectors.toList());

        entriesOlderThanAnHour.forEach(entry -> this.cachedForecasts.remove(entry.getKey()));

        if (!this.cachedForecasts.containsKey(key)) {
            if (this.cachedForecasts.size() == WeatherClient.MAX_CACHE_LIMIT) {
                Date oldestDate = new Date();
                String oldestForecastKey = "";

                for (final String cachedKey : this.cachedForecasts.keySet()) {
                    if (this.cachedForecasts.get(cachedKey).getTimeAdded().before(oldestDate)) {
                        oldestDate = this.cachedForecasts.get(cachedKey).getTimeAdded();
                        oldestForecastKey = cachedKey;
                    }
                }

                this.cachedForecasts.remove(oldestForecastKey);
            }

            this.cachedForecasts.put(key, new CustomForecast(this.forecaster.forecastFor(region, day), new Date()));
        }

        return this.cachedForecasts.get(key);
    }
}

class CustomForecast {
    final Forecast forecast;
    final Date timeAdded;

    public CustomForecast(final Forecast forecast, final Date timeAdded) {
        this.forecast = forecast;
        this.timeAdded = timeAdded;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public Date getTimeAdded() {
        return timeAdded;
    }
}


