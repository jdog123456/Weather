package client;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import com.weather.Day;
import com.weather.Forecast;
import com.weather.Forecaster;
import com.weather.Region;

public class WeatherClientTest {

//    @Test
//    public void shouldNotBeCached() {
//        final Forecaster mockForecaster = Mockito.mock(Forecaster.class);
//        final Forecast mockForecast = new Forecast("summary", 15);
//        final WeatherClient client = new WeatherClient(mockForecaster);
//
//        BDDMockito.given(mockForecaster.forecastFor(Region.LONDON, Day.SUNDAY))
//            .willReturn(mockForecast);
//
//        final Forecast result = client.getForecast(Region.LONDON, Day.SUNDAY);
//
//        Assert.assertEquals(mockForecast, result);
//    }
//
//    @Test
//    public void shouldBeCached() {
//        final Forecaster mockForecaster = Mockito.mock(Forecaster.class);
//        final Forecast mockForecast = new Forecast("summary", 15);
//        final WeatherClient client = new WeatherClient(mockForecaster);
//
//        BDDMockito.given(mockForecaster.forecastFor(Region.LONDON, Day.SUNDAY))
//            .willReturn(mockForecast);
//
//        final Forecast result1 = client.getForecast(Region.LONDON, Day.SUNDAY);
//        final Forecast result2 = client.getForecast(Region.LONDON, Day.SUNDAY);
//
//        Mockito.verify(mockForecaster, Mockito.times(1))
//            .forecastFor(Region.LONDON, Day.SUNDAY);
//
//        Assert.assertEquals(result1, result2);
//    }

    @Test
    public void shouldOverwriteOldEntriesWithNewOnesIfMaxLimitIsReached() {
        final Forecaster mockForecaster = Mockito.mock(Forecaster.class);
        final Forecast mockForecast = new Forecast("summary", 15);
        final WeatherClient client = new WeatherClient(mockForecaster);

        BDDMockito.given(mockForecaster.forecastFor(Region.LONDON, Day.SUNDAY))
            .willReturn(mockForecast);
        BDDMockito.given(mockForecaster.forecastFor(Region.BIRMINGHAM, Day.SUNDAY))
            .willReturn(mockForecast);
        BDDMockito.given(mockForecaster.forecastFor(Region.EDINBURGH, Day.SUNDAY))
            .willReturn(mockForecast);

        client.getForecast(Region.LONDON, Day.SUNDAY);
        Mockito.verify(mockForecaster, Mockito.times(1))
            .forecastFor(Region.LONDON, Day.SUNDAY);
        Assert.assertEquals(1, client.cachedForecasts.size());

        client.getForecast(Region.BIRMINGHAM, Day.SUNDAY);
        Mockito.verify(mockForecaster, Mockito.times(1))
            .forecastFor(Region.BIRMINGHAM, Day.SUNDAY);
        Assert.assertEquals(2, client.cachedForecasts.size());

        client.getForecast(Region.EDINBURGH, Day.SUNDAY);
        Mockito.verify(mockForecaster, Mockito.times(1))
            .forecastFor(Region.EDINBURGH, Day.SUNDAY);
        Assert.assertEquals(2, client.cachedForecasts.size());
    }

    @Test
    public void shouldRemoveItemsIfTheyAreMoreThanAnHourOld() {
        final Forecaster mockForecaster = Mockito.mock(Forecaster.class);
        final Forecast mockForecast = new Forecast("summary", 15);
        final WeatherClient client = new WeatherClient(mockForecaster);

        BDDMockito.given(mockForecaster.forecastFor(Region.LONDON, Day.SUNDAY))
            .willReturn(mockForecast);

        client.cachedForecasts.put("LONDON-SUNDAY", new CustomForecast(mockForecast,
            new Date(new Date().toInstant().minusSeconds(360L).getEpochSecond())));

        client.getForecast(Region.LONDON, Day.SUNDAY);
        Mockito.verify(mockForecaster, Mockito.times(1))
            .forecastFor(Region.LONDON, Day.SUNDAY);
        Assert.assertEquals(1, client.cachedForecasts.size());
    }
}