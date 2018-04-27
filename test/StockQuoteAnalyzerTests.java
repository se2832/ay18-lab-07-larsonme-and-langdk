import exceptions.InvalidAnalysisState;
import exceptions.InvalidStockSymbolException;
import exceptions.StockTickerConnectionError;
import org.testng.annotations.Test;

import org.testng.annotations.BeforeMethod;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.testng.Assert;

import org.mockito.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import javax.xml.crypto.Data;

public class StockQuoteAnalyzerTests {
	@Mock
	private StockQuoteGeneratorInterface mockedStockQuoteGenerator;
	@Mock
	private StockTickerAudioInterface mockedStockTickerAudio;

	private StockQuoteAnalyzer analyzer;

	@BeforeMethod
	public void beforeMethod() {
		mockedStockQuoteGenerator = mock(StockQuoteGeneratorInterface.class);
		mockedStockTickerAudio = mock(StockTickerAudioInterface.class);
	}

	@AfterMethod
	public void afterMethod() {
		mockedStockQuoteGenerator = null;
		mockedStockTickerAudio = null;

	}

	@Test
	public void testShouldReturnStockQuoteAnalyzerObjectWhenConstructedWithValidInputs() throws InvalidStockSymbolException, StockTickerConnectionError {
		//Arrange
		//Act
		analyzer = new StockQuoteAnalyzer("F",mockedStockQuoteGenerator,mockedStockTickerAudio);

		//Assert
		Assert.assertEquals(analyzer.getClass(),StockQuoteAnalyzer.class);
	}

	@Test(expectedExceptions = StockTickerConnectionError.class)
	public void testShouldThrowStockTickerConnectionErrorWhenConstructorIsUnabletoConnectToStockQuoteSource() throws InvalidStockSymbolException, StockTickerConnectionError {
		//Arrange
		//Act
		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);
		//TODO Figure this one out
		//Assert
	}
	//This tests issue #7
	@Test(expectedExceptions = InvalidStockSymbolException.class)
	public void testShouldThrowInvalidStockSymbolExceptionWhenConstructorIsPassedNullValueForStockSymbol() throws InvalidStockSymbolException, StockTickerConnectionError {
		//Arrange
		//Act
		analyzer = new StockQuoteAnalyzer(null, mockedStockQuoteGenerator, mockedStockTickerAudio);
		//Assert
	}



	//Tests issue number 1
	@Test(expectedExceptions = InvalidStockSymbolException.class)
	public void testShouldThrowExceptionWhenConstructingWithInvalidStockSymbol() throws NullPointerException, InvalidStockSymbolException, StockTickerConnectionError
	{
		//Arrange
        //Act
		analyzer = new StockQuoteAnalyzer("ZZZZZZZZZ", mockedStockQuoteGenerator, mockedStockTickerAudio);

		//Assert
	}
	//Test issue number 2
	@Test(expectedExceptions = NullPointerException.class)
	public void testShouldThrowExceptionWhenConstructingWithNullSource() throws NullPointerException, InvalidStockSymbolException, StockTickerConnectionError
	{
        //Arrange
        //Act
		analyzer = new StockQuoteAnalyzer("DIS", null, mockedStockTickerAudio);

		//Assert
	}

	@Test(expectedExceptions = StockTickerConnectionError.class)
	public void testShouldThrowExceptionWhenRefreshConnectionError() throws StockTickerConnectionError, NullPointerException, InvalidStockSymbolException, Exception
	{
		// Arrange
		when(mockedStockQuoteGenerator.getCurrentQuote()).thenThrow(new Exception());
        analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

		// Act - Instantiate the class to test.
		analyzer.refresh();

		//Assert
	}

	//Test for issue #3
	@Test(expectedExceptions = InvalidAnalysisState.class)
	public void testShouldThrowExceptionWhenGetPreviousOpenInvalidAnalysisState() throws InvalidAnalysisState, NullPointerException, InvalidStockSymbolException, StockTickerConnectionError
	{
        //Arrange
        analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

        //Act
		when(analyzer.getPreviousOpen()).thenReturn(null);


		//Assert
	}
	
	@Test(expectedExceptions = InvalidAnalysisState.class)
	public void testShouldThrowExceptionWhenGetCurrentPriceInvalidAnalysisState() throws InvalidAnalysisState, NullPointerException, InvalidStockSymbolException, StockTickerConnectionError
	{
        //Arrange
		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

        //Act
		when(analyzer.getCurrentPrice()).thenReturn(null);

        //Assert
	}
	
	@Test(expectedExceptions = InvalidAnalysisState.class)
	public void testShouldThrowExceptionWhenGetChangeSinceOpenInvalidAnalysisState() throws InvalidAnalysisState, NullPointerException, InvalidStockSymbolException, StockTickerConnectionError
	{
        //Arrange
		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

        //Act
		when(analyzer.getChangeSinceOpen()).thenReturn(null);

		//Assert
	}
	
	@Test(expectedExceptions = InvalidAnalysisState.class)
	public void testShouldThrowExceptionWhenGetPercentChangeSinceOpenInvalidAnalysisState() throws InvalidAnalysisState, NullPointerException, InvalidStockSymbolException, StockTickerConnectionError
	{
        //Arrange
		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

		//Act
		when(analyzer.getPercentChangeSinceOpen()).thenReturn(null);

		//Assert
	}
	
	@Test(expectedExceptions = InvalidAnalysisState.class)
	public void testShouldThrowExceptionWhenGetChangeSinceLastCheckNoUpdates() throws InvalidAnalysisState, NullPointerException, InvalidStockSymbolException, StockTickerConnectionError, Exception
	{
        //Arrange
		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);
		when(mockedStockQuoteGenerator.getCurrentQuote()).thenReturn(null);


		//Act
		analyzer.getChangeSinceLastCheck();

	}

	@Test
	public void testShouldPlayAppropriateAudioWhenNoValidUpdates() throws Exception
	{
		// Arrange - setup the expected calls.
        //TODO Telling the mocked generator to return null when getCurrentQuote is called
		when(mockedStockQuoteGenerator.getCurrentQuote()).thenReturn(null);
		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

		// Act
		analyzer.playAppropriateAudio();


		// Assert - verify that method was never called on a mock
		verify(mockedStockTickerAudio, times(1)).playErrorMusic();
		verify(mockedStockTickerAudio, times(0)).playHappyMusic();
		verify(mockedStockTickerAudio, times(0)).playSadMusic();
	}

	//Test for issue #4
    @Test(expectedExceptions = InvalidAnalysisState.class)
	public void testShouldGetChangeSinceLastCheckOneUpdate() throws Exception
	{
		// Arrange - Setup the expected calls.
		when(mockedStockQuoteGenerator.getCurrentQuote()).thenReturn(new StockQuote("F", 100.00, 100.00, 0.00));
		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

        // Act
		analyzer.refresh();

		// Assert
		analyzer.getChangeSinceLastCheck();
		Assert.assertEquals(0.0, analyzer.getChangeSinceLastCheck());
	}

	//This tests to make sure no audio is played if the stock ticker audio is null
	@Test(dataProvider = "normalOperationDataProvider")
	public void playAppropriateSoundShouldNotPlayAnyMusicIfStockTickerAudioIsNull(StockQuote firstReturn, StockQuote secondReturn, int happyMusicCount, int sadMusicCount,
																				  double percentChange) throws Exception {
		when(mockedStockQuoteGenerator.getCurrentQuote()).thenReturn(firstReturn, secondReturn);

		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, null);
// verify that method was never called on a mock
		verify(mockedStockTickerAudio, never()).playErrorMusic();
		//This tests issue #5
		verify(mockedStockTickerAudio, never()).playHappyMusic();
		//This tests issue #6
		verify(mockedStockTickerAudio, never()).playSadMusic();
	}


	@DataProvider
	public Object[][] normalOperationDataProvider() {
		return new Object[][] {
				{ new StockQuote("F", 100.00, 100.00, 0.00), new StockQuote("F", 100.00, 100.00, 0.00), 0, 0, 0.0 }, // No
//																														// change.
				{ new StockQuote("F", 100.00, 100.00, 100.00), new StockQuote("F", 100.00, 100.99, 0.99), 0, 0, 0.99 }, // .99%
				// increase
				{ new StockQuote("F", 100.00, 100.00, 100.00), new StockQuote("F", 100.00, 101.00, 1.0), 0, 0, 1.0 }, // 1.0%
																														// increase
				{ new StockQuote("F", 100.00, 100.00, 100.00), new StockQuote("F", 100.00, 101.01, 1.01), 1, 0, 1.01 }, // 1.01%
				// increase
				{ new StockQuote("F", 100.00, 100.00, 100.00), new StockQuote("F", 100.00, 110.00, 10.00), 1, 0, 10.0 }, // 10.0%
				// increase
				//Next 5 object[] are BVA testing for happy sounds playing.  Deal with change in price and not percentage
				{ new StockQuote("F", 200.00, 200.00, 200.00), new StockQuote("F", 200.00, 200.00, 0.00), 0, 0, 0.0 }, // None
//																														// change.
				{ new StockQuote("F", 200.00, 200.00, 200.00), new StockQuote("F", 200.00, 200.99, 0.99), 0, 0, 0.49 }, // 0.99
				// increase
				{ new StockQuote("F", 200.00, 200.00, 200.00), new StockQuote("F", 200.00, 201.00, 1.0), 0, 0, 0.5 }, // 1.00
//				// increase
				{ new StockQuote("F", 200.00, 200.00, 200.00), new StockQuote("F", 200.00, 201.01, 1.01), 1, 0, 0.51 }, // 1.01
//				// increase
				{ new StockQuote("F", 200.00, 200.00, 200.00), new StockQuote("F", 200.00, 210.00, 10.00), 1, 0, 5.0 }, // 10.0
//

				{ new StockQuote("F", 100.00, 100.00, 0.00), new StockQuote("F", 100.00, 100.00, 0.00), 0, 0, 0.0 }, // No
																														// change.
				{ new StockQuote("F", 100.00, 100.00, 100.00), new StockQuote("F", 100.00, 99.01, -0.99), 0, 0, -0.99 }, // .99%
				// decrease
				{ new StockQuote("F", 100.00, 100.00, 100.00), new StockQuote("F", 100.00, 99.00, -1.0), 0, 1, -1.0 }, // 1.0%
																														// decrease
				{ new StockQuote("F", 100.00, 100.00, 100.00), new StockQuote("F", 100.00, 98.99, -1.01), 0, 1, -1.01 }, // 1.01%
				// decrease
				{ new StockQuote("F", 100.00, 100.00, 100.00), new StockQuote("F", 100.00, 90.00, -10.0), 0, 1, -10.00 }, // 10.0%
				// decrease
				//Below here is BVA for change.  Used heavily for BVA in testing for what sounds should play
				{ new StockQuote("F", 200.00, 200.00, 0.00), new StockQuote("F", 200.00, 200.00, 0.00), 0, 0, 0.0 }, // None
//				// change.
				{ new StockQuote("F", 200.00, 200.00, 200.00), new StockQuote("F", 200.00, 199.01, -0.99), 0, 0, -0.49 }, // -0.99
//				 decrease
				{ new StockQuote("F", 200.00, 200.00, 200.00), new StockQuote("F", 200.00, 199.00, -1.0), 0, 0, -0.50 }, // -1.0
				// decrease
				{ new StockQuote("F", 200.00, 200.00, 200.00), new StockQuote("F", 200.00, 198.99, -1.01), 0, 1, -0.51 }, // -1.01
				// decrease
				{ new StockQuote("F", 200.00, 200.00, 200.00), new StockQuote("F", 200.00, 190.00, -10.0), 0, 1, -5.00 }, // -10.0
//				// decrease
		};
	}
	//Tests issue #5 and #6
	@Test(dataProvider = "normalOperationDataProvider")
	public void testGetPercentChangeSinceLastOpenShouldReturnCorrectPercentChangedWhenCalled(StockQuote firstReturn, StockQuote secondReturn, int happyMusicCount, int sadMusicCount,
			double percentChange) throws Exception {

	    // Arrange
		when(mockedStockQuoteGenerator.getCurrentQuote()).thenReturn(firstReturn, secondReturn);

		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

		// Act
		analyzer.refresh();
		analyzer.refresh();
		analyzer.playAppropriateAudio();

		// Assert
        // Now verify the methods were called or not called appropriately
		// default call count is 1
		// check if add function is called three times
		verify(mockedStockQuoteGenerator, times(2)).getCurrentQuote();

		// verify that method was never called on a mock
		verify(mockedStockTickerAudio, never()).playErrorMusic();
		//This tests issue #5
		verify(mockedStockTickerAudio, times(happyMusicCount)).playHappyMusic();
		//This tests issue #6
		verify(mockedStockTickerAudio, times(sadMusicCount)).playSadMusic();

		// Now check that the change calculation was correct.
		Assert.assertEquals(analyzer.getPercentChangeSinceOpen(), percentChange, 0.01);
	}
	
	
	@Test(dataProvider = "normalOperationDataProvider")
	public void testGetChangeSinceLastCheckShouldReturnCorrectChangeWhenCalled(StockQuote firstReturn, StockQuote secondReturn, int happyMusicCount, int sadMusicCount,
			double percentChange) throws Exception {

	    // Arrange
		when(mockedStockQuoteGenerator.getCurrentQuote()).thenReturn(firstReturn, secondReturn);
		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

        // Act
		analyzer.refresh();
		analyzer.refresh();

		// Assert - Now check that the change calculation was correct.
		Assert.assertEquals(analyzer.getChangeSinceLastCheck(), secondReturn.getLastTrade()-firstReturn.getLastTrade(), 0.01);
	}
	
	@Test(dataProvider = "normalOperationDataProvider")
	public void testGetChangeSinceOpenShouldReturnCorrectChangeWhenCalled(StockQuote firstReturn, StockQuote secondReturn, int happyMusicCount, int sadMusicCount,
			double percentChange) throws Exception {
		// Arrange - Setup the expected calls.
		when(mockedStockQuoteGenerator.getCurrentQuote()).thenReturn(firstReturn);
		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

        // Act
		analyzer.refresh();

		// Assert - Now check that the change calculation was correct.
        Assert.assertEquals(analyzer.getChangeSinceOpen(), firstReturn.getChange(), 0.01);
	}
	
	@Test(dataProvider = "normalOperationDataProvider")
	public void testGetCurrentPriceShouldReturnCurrentPriceWhenCalled(StockQuote firstReturn, StockQuote secondReturn, int happyMusicCount, int sadMusicCount,
			double percentChange) throws Exception {
		// Arrange
		when(mockedStockQuoteGenerator.getCurrentQuote()).thenReturn(firstReturn);
		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

        // Act
		analyzer.refresh();

		// Assert
        Assert.assertEquals(analyzer.getCurrentPrice(), firstReturn.getLastTrade(), 0.01);
	}
	
	@Test(dataProvider = "normalOperationDataProvider")
	public void testGetPreviousOpenShouldReturnCorrectDataWhenCalled(StockQuote firstReturn, StockQuote secondReturn, int happyMusicCount, int sadMusicCount,
			double percentChange) throws Exception {

	    // Assert
		when(mockedStockQuoteGenerator.getCurrentQuote()).thenReturn(firstReturn);
		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

        // Act
		analyzer.refresh();


		// Assert
        Assert.assertEquals(analyzer.getPreviousOpen(), firstReturn.getOpen(), 0.01);
	}

	@Test(dataProvider = "normalOperationDataProvider")
	public void testGetCurrentQuoteShouldReturnCorrectDataWhenCalled(StockQuote firstReturn, StockQuote secondReturn, int happyMusicCount, int sadMusicCount,
																	 double percentChange) throws Exception {

		// Assert
		when(mockedStockQuoteGenerator.getCurrentQuote()).thenReturn(firstReturn);
		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

		// Act
		analyzer.refresh();

		String expected =firstReturn.toString();
		String actual = analyzer.getCurrentQuote().toString();


		// Assert
		Assert.assertEquals(expected, actual);
	}

	@Test(dataProvider = "normalOperationDataProvider")
	public void testGetSymbolShouldReturnCorrectSymbol(StockQuote firstReturn, StockQuote secondReturn, int happyMusicCount, int sadMusicCount,
																	 double percentChange) throws Exception {

		// Assert
		when(mockedStockQuoteGenerator.getSymbol()).thenReturn(firstReturn.getSymbol());
		analyzer = new StockQuoteAnalyzer("F", mockedStockQuoteGenerator, mockedStockTickerAudio);

		// Act
		analyzer.refresh();

		String expected =firstReturn.getSymbol();
		String actual = analyzer.getSymbol();


		// Assert
		Assert.assertEquals(expected, actual);
	}

}
