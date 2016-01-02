/**
 * Copyright 2015 Knowm Inc. (http://knowm.org) and contributors.
 * Copyright 2011-2015 Xeiam LLC (http://xeiam.com) and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.knowm.xchart.internal.chartpart;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.knowm.xchart.StyleManager;
import org.knowm.xchart.internal.Utils;
import org.knowm.xchart.internal.chartpart.Axis.AxisType;
import org.knowm.xchart.internal.chartpart.Axis.Direction;

/**
 * This class encapsulates the logic to generate the axis tick mark and axis tick label data for rendering the axis ticks for String axes
 *
 * @author timmolter
 */
public class AxisTickCalculator_Category extends AxisTickCalculator {

  /**
   * Constructor
   *
   * @param axisDirection
   * @param workingSpace
   * @param categories
   * @param axisType
   * @param styleManager
   */
  public AxisTickCalculator_Category(Direction axisDirection, double workingSpace, List<?> categories, AxisType axisType, StyleManager styleManager) {

    super(axisDirection, workingSpace, Double.NaN, Double.NaN, styleManager);

    calculate(categories, axisType);
  }

  private void calculate(List<?> categories, AxisType axisType) {

    // tick space - a percentage of the working space available for ticks
    int tickSpace = (int) (styleManager.getAxisTickSpacePercentage() * workingSpace); // in plot space

    // where the tick should begin in the working space in pixels
    double margin = Utils.getTickStartOffset(workingSpace, tickSpace);

    // generate all tickLabels and tickLocations from the first to last position
    double gridStep = (tickSpace / (double) categories.size());
    double firstPosition = gridStep / 2.0;

    // set up String formatters that may be encountered
    NumberFormatter numberFormatter = null;
    SimpleDateFormat simpleDateformat = null;
    if (axisType == AxisType.Number) {
      numberFormatter = new NumberFormatter(styleManager);
    }
    else if (axisType == AxisType.Date) {
      if (styleManager.getDatePattern() == null) {
        throw new RuntimeException("You need to set the Date Formatting Pattern!!!");
      }
      simpleDateformat = new SimpleDateFormat(styleManager.getDatePattern(), styleManager.getLocale());
      simpleDateformat.setTimeZone(styleManager.getTimezone());
    }

    int counter = 0;

    for (Object category : categories) {
      if (axisType == AxisType.String) {
        tickLabels.add(category.toString());
        double tickLabelPosition = margin + firstPosition + gridStep * counter++;
        tickLocations.add(tickLabelPosition);
      }
      else if (axisType == AxisType.Number) {
        tickLabels.add(numberFormatter.formatNumber(new BigDecimal(category.toString()), minValue, maxValue, axisDirection));
      }
      else if (axisType == AxisType.Date) {

        tickLabels.add(simpleDateformat.format((((Date) category).getTime())));
      }
      double tickLabelPosition = (int) (margin + firstPosition + gridStep * counter++);
      tickLocations.add(tickLabelPosition);
    }

  }
}