/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 fx-market-making (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.tools4j.fx.make.base;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tools4j.fx.make.api.Currency;
import org.tools4j.fx.make.impl.CurrencyPair;

/**
 * Unit test for {@link MarketRates}
 */
public class MarketRatesTest {
	
	private PairAndRate[] pairsAndRates;
	private MarketRates marketRates;
	
	@Before
	public void beforeEach() {
		//test data
		pairsAndRates = new PairAndRate[] {
				new PairAndRate(Currency.AUD, Currency.USD, 0.7307),
				new PairAndRate(Currency.AUD, Currency.NZD, 1.1081),
				new PairAndRate(Currency.EUR, Currency.USD, 1.1010),
				new PairAndRate(Currency.USD, Currency.JPY, 123.92),
				new PairAndRate(Currency.USD, Currency.CAD, 1.3089),
		};
		//construct MarketRates
		final Map<CurrencyPair, Double> rates = new LinkedHashMap<>();
		for (final PairAndRate pairAndRate : pairsAndRates) {
			rates.put(pairAndRate.currencyPair, pairAndRate.rate);
		}
		this.marketRates = new MarketRates(rates);
	}

	@Test
	public void shouldFindSelfRate() {
		//given
		final MarketRates emptyRates = new MarketRates(Collections.emptyMap());
		
		//when + then
		for (final Currency currency : Currency.values()) {
			//when
			final double rate = emptyRates.getRate(currency, currency);
			//then
			Assert.assertEquals("self-rate should be 1", 1.0, rate, 0.0);
			//when
			final double again = marketRates.getRate(currency, currency);
			//then
			Assert.assertEquals("self-rate should be 1", 1.0, again, 0.0);
		}
	}

	@Test
	public void shouldFindSingleDirectAndIndirectRate() {
		//given
		final CurrencyPair audUsd = new CurrencyPair(Currency.AUD, Currency.USD);
		final double rate = 0.7307;
		final MarketRates rates = new MarketRates(Collections.singletonMap(audUsd, rate));
		
		//when
		final double direct = rates.getRate(Currency.AUD, Currency.USD);
		//then
		Assert.assertEquals("unexpected direct rate" , rate, direct, 0.0);
		//when
		final double indirect = rates.getRate(Currency.USD, Currency.AUD);
		//then
		Assert.assertEquals("unexpected indirect rate" , 1/rate, indirect, 0.0);
	}

	@Test
	public void shouldFindDirectRates() {
		//when + then
		for (final PairAndRate pairAndRate : pairsAndRates) {
			//when
			final double direct = marketRates.getRate(pairAndRate.currencyPair.getBase(), pairAndRate.currencyPair.getTerms());
			//then
			Assert.assertEquals("unexpected direct rate for " + pairAndRate.currencyPair , pairAndRate.rate, direct, 0.0);
		}
	}
	
	@Test
	public void shouldFindIndirectRates() {
		//when + then
		for (final PairAndRate pairAndRate : pairsAndRates) {
			//when
			final double indirect = marketRates.getRate(pairAndRate.currencyPair.getTerms(), pairAndRate.currencyPair.getBase());
			//then
			Assert.assertEquals("unexpected indirect rate for " + pairAndRate.currencyPair , 1/pairAndRate.rate, indirect, 0.0);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfRateNotFound() {
		//when
		marketRates.getRate(Currency.USD, Currency.CHF);
		//then: exception
	}

	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionForNullAsset1() {
		//when
		marketRates.getRate(null, Currency.USD);
		//then: exception
	}
	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionForNullAsset2() {
		//when
		marketRates.getRate(Currency.USD, null);
		//then: exception
	}
	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionForNullRateInRatesMap() {
		//when
		new MarketRates(Collections.singletonMap(new CurrencyPair(Currency.AUD, Currency.USD), (Double)null));
		//then: exception
	}

	private static final class PairAndRate {
		public final CurrencyPair currencyPair;
		public final double rate;
		public PairAndRate(Currency base, Currency terms, double rate) {
			this.currencyPair = new CurrencyPair(base, terms);
			this.rate = rate;
		}
	}
}
