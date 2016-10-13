package com.tickaroo.tikxml.regressiontests;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * A Element that skips some inner elements
 *
 * @author Hannes Dorfmann
 */
@Xml class Team {

  @Attribute int id;
  @Attribute String countryId;
  @Attribute String shortName;
  @Attribute String longName;
  @Attribute String token;
  @Attribute String iconSmall;
  @Attribute String iconBig;
  @Attribute int defaultLeagueId;
  @Attribute double lat;
  @Attribute double lng;
}
