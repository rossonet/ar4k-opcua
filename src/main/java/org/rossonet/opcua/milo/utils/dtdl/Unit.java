package org.rossonet.opcua.milo.utils.dtdl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Lists;

public enum Unit {
	metrePerSecondSquared, centimetrePerSecondSquared, gForce, radianPerSecondSquared, radianPerSecond, degreePerSecond,
	revolutionPerSecond, revolutionPerMinute, squareMetre, squareCentimetre, squareMillimetre, squareKilometre, hectare,
	squareFoot, squareInch, acre, farad, millifarad, microfarad, nanofarad, picofarad, ampere, microampere, milliampere,
	bitPerSecond, kibibitPerSecond, mebibitPerSecond, gibibitPerSecond, tebibitPerSecond, exbibitPerSecond,
	zebibitPerSecond, yobibitPerSecond, bytePerSecond, kibibytePerSecond, mebibytePerSecond, gibibytePerSecond,
	tebibytePerSecond, exbibytePerSecond, zebibytePerSecond, yobibytePerSecond, bit, kibibit, mebibit, gibibit, tebibit,
	exbibit, zebibit, yobibit, _byte, kibibyte, mebibyte, gibibyte, tebibyte, exbibyte, zebibyte, yobibyte, coulomb,
	joule, kilojoule, megajoule, gigajoule, electronvolt, megaelectronvolt, kilowattHour, hertz, kilohertz, megahertz,
	gigahertz, kilogramPerCubicMetre, gramPerCubicMetre, lux, footcandle, henry, millihenry, microhenry, secondOfArc,
	turn, metre, centimetre, millimetre, micrometre, nanometre, kilometre, foot, inch, mile, nauticalMile,
	astronomicalUnit, candelaPerSquareMetre, gigawatt, horsepower, lumen, candela, weber, maxwell, tesla, kilogram,
	gram, milligram, microgram, tonne, slug, gramPerSecond, kilogramPerSecond, gramPerHour, kilogramPerHour, watt,
	microwatt, milliwatt, kilowatt, megawatt, kilowattHourPerYear, pascal, kilopascal, bar, millibar,
	millimetresOfMercury, poundPerSquareInch, inchesOfMercury, inchesOfWater, unity, percent, ohm, milliohm, kiloohm,
	megaohm, decibel, bel, kelvin, degreeCelsius, degreeFahrenheit, newton, pound, ounce, ton, second, millisecond,
	microsecond, nanosecond, minute, hour, day, year, newtonMetre, metrePerSecond, centimetrePerSecond,
	kilometrePerSecond, metrePerHour, kilometrePerHour, milePerHour, milePerSecond, knot, volt, millivolt, microvolt,
	kilovolt, megavolt, cubicMetre, cubicCentimetre, litre, millilitre, cubicFoot, cubicInch, fluidOunce, gallon,
	litrePerSecond, millilitrePerSecond, litrePerHour, millilitrePerHour, radian, degreeOfArc, minuteOfArc;

	public static Unit getUnit(String value) {
		final String checkValue = (value.equals("byte") ? "_byte" : value);
		return Unit.valueOf(checkValue);
	}

	public Collection<UnitType> getUnitTypes() {
		final Set<UnitType> result = new HashSet<>();
		for (final UnitType u : UnitType.values()) {
			if (Lists.newArrayList(u.getUnits()).contains(this)) {
				result.add(u);
			}
		}
		return result;
	}
}
