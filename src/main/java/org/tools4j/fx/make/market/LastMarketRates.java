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
package org.tools4j.fx.make.market;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.tools4j.fx.make.asset.AssetPair;
import org.tools4j.fx.make.execution.Deal;
import org.tools4j.fx.make.execution.Order;
import org.tools4j.fx.make.position.MarketSnapshot;
import org.tools4j.fx.make.position.MarketSnapshotImpl;

/**
 * Observes the market and registers deals as last market rates.
 */
public class LastMarketRates implements MarketObserver {
	
	private final Map<AssetPair<?, ?>, Double> lastRates = new ConcurrentHashMap<>();
	
	@Override
	public void onDeal(Deal deal) {
		lastRates.put(deal.getAssetPair(), deal.getPrice());
	}
	
	@Override
	public void onOrder(Order order) {
		//don't include orders
	}
	
	public MarketSnapshot getMarketSnapshot() {
		return new MarketSnapshotImpl(lastRates);
	}
	
}