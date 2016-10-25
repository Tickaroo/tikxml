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

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Team)) return false;

    Team team = (Team) o;

    if (id != team.id) return false;
    if (defaultLeagueId != team.defaultLeagueId) return false;
    if (Double.compare(team.lat, lat) != 0) return false;
    if (Double.compare(team.lng, lng) != 0) return false;
    if (countryId != null ? !countryId.equals(team.countryId) : team.countryId != null)
      return false;
    if (shortName != null ? !shortName.equals(team.shortName) : team.shortName != null)
      return false;
    if (longName != null ? !longName.equals(team.longName) : team.longName != null) return false;
    if (token != null ? !token.equals(team.token) : team.token != null) return false;
    if (iconSmall != null ? !iconSmall.equals(team.iconSmall) : team.iconSmall != null)
      return false;
    return iconBig != null ? iconBig.equals(team.iconBig) : team.iconBig == null;
  }

  @Override public int hashCode() {
    int result;
    long temp;
    result = id;
    result = 31 * result + (countryId != null ? countryId.hashCode() : 0);
    result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
    result = 31 * result + (longName != null ? longName.hashCode() : 0);
    result = 31 * result + (token != null ? token.hashCode() : 0);
    result = 31 * result + (iconSmall != null ? iconSmall.hashCode() : 0);
    result = 31 * result + (iconBig != null ? iconBig.hashCode() : 0);
    result = 31 * result + defaultLeagueId;
    temp = Double.doubleToLongBits(lat);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(lng);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }
}
