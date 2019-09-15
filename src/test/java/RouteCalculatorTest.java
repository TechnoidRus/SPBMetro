import core.Line;
import core.Station;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import junit.framework.TestCase;

public class RouteCalculatorTest extends TestCase {

  List<Station> threeTransfersRoute;
  List<Station> twoTransfersRoute;
  List<Station> noTransferRoute;

  StationIndex stationIndex;
  RouteCalculator calculator;

  Station moskovskaya;
  Station sadovaya;
  Station omskaya;
  Station ozernaya;


  @Override
  public void setUp() throws Exception {

    //Схема тестовой линии
    //[l1]            [l2]            [l3]
    //[l1]-[transfer]-[l2]            [l3]
    //[l1]            [l2]-[transfer]-[l3]

    stationIndex = new StationIndex();

    Line line1 = new Line(1, "Первая");
    Line line2 = new Line(2, "Вторая");
    Line line3 = new Line(3, "Третья");

    moskovskaya = new Station("Московская", line1);
    Station kyrskaya = new Station("Курская", line1);
    omskaya = new Station("Омская", line1);
    ozernaya = new Station("Озерная", line2);
    Station zaprudnaya = new Station("Запрудная", line2);
    Station morskaya = new Station("Морская", line2);
    sadovaya = new Station("Садовая", line3);
    Station teplichanaya = new Station("Тепличная", line3);
    Station ogorodnaya = new Station("Огородная", line3);

    Stream.of(line1, line2, line3).forEach(stationIndex::addLine);
    Stream
        .of(moskovskaya, kyrskaya, omskaya, ozernaya, zaprudnaya, morskaya, sadovaya, teplichanaya,
            ogorodnaya).peek(s -> s.getLine().addStation(s)).forEach(stationIndex::addStation);
    stationIndex.addConnection(Stream.of(kyrskaya, zaprudnaya).collect(Collectors.toList()));
    stationIndex.addConnection(Stream.of(morskaya, ogorodnaya).collect(Collectors.toList()));
    stationIndex.getConnectedStations(kyrskaya);
    stationIndex.getConnectedStations(morskaya);

    calculator = new RouteCalculator(stationIndex);

    //тестовые маршруты
    noTransferRoute = Stream.of(moskovskaya, kyrskaya, omskaya).collect(Collectors.toList());
    twoTransfersRoute = Stream.of(moskovskaya, kyrskaya, zaprudnaya, ozernaya)
        .collect(Collectors.toList());
    threeTransfersRoute = Stream
        .of(moskovskaya, kyrskaya, zaprudnaya, morskaya, ogorodnaya, teplichanaya, sadovaya)
        .collect(Collectors.toList());
  }

  public void testCalculateDuration() {
    double actual = RouteCalculator.calculateDuration(threeTransfersRoute);
    double expected = 17;
    assertEquals(expected, actual);
  }

  public void testGetShortestRoute() {
    List<Station> actualNoTransfer = calculator.getShortestRoute(moskovskaya, omskaya);
    List<Station> actualTwoTransfer = calculator.getShortestRoute(moskovskaya, ozernaya);
    List<Station> actualThreeTransfers = calculator.getShortestRoute(moskovskaya, sadovaya);

    List<Station> expectedNoTransfers = noTransferRoute;
    List<Station> expectedTwoTransfers = twoTransfersRoute;
    List<Station> expectedThreeTransfers = threeTransfersRoute;

    assertEquals(actualNoTransfer, expectedNoTransfers);
    assertEquals(actualTwoTransfer, expectedTwoTransfers);
    assertEquals(actualThreeTransfers, expectedThreeTransfers);
  }


}
