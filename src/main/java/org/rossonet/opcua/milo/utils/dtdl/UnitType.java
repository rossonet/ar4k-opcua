package org.rossonet.opcua.milo.utils.dtdl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public enum UnitType {
	AccelerationUnit(new Unit[] { Unit.metrePerSecondSquared, Unit.centimetrePerSecondSquared, Unit.gForce }),
	AngleUnit(new Unit[] { Unit.radian, Unit.degreeOfArc, Unit.minuteOfArc, Unit.secondOfArc, Unit.turn }),
	AngularAccelerationUnit(new Unit[] { Unit.radianPerSecondSquared }),
	AngularVelocityUnit(new Unit[] { Unit.radianPerSecond, Unit.degreePerSecond, Unit.revolutionPerSecond,
			Unit.revolutionPerMinute }),
	AreaUnit(new Unit[] { Unit.squareMetre, Unit.squareCentimetre, Unit.squareMillimetre, Unit.squareKilometre,
			Unit.hectare, Unit.squareFoot, Unit.squareInch, Unit.acre }),
	CapacitanceUnit(new Unit[] { Unit.farad, Unit.millifarad, Unit.microfarad, Unit.nanofarad, Unit.picofarad }),
	CurrentUnit(new Unit[] { Unit.ampere, Unit.microampere, Unit.milliampere }),
	DataRateUnit(new Unit[] { Unit.bitPerSecond, Unit.kibibitPerSecond, Unit.mebibitPerSecond, Unit.gibibitPerSecond,
			Unit.tebibitPerSecond, Unit.exbibitPerSecond, Unit.zebibitPerSecond, Unit.yobibitPerSecond,
			Unit.bytePerSecond, Unit.kibibytePerSecond, Unit.mebibytePerSecond, Unit.gibibytePerSecond,
			Unit.tebibytePerSecond, Unit.exbibytePerSecond, Unit.zebibytePerSecond, Unit.yobibytePerSecond }),
	DataSizeUnit(new Unit[] { Unit.bit, Unit.kibibit, Unit.mebibit, Unit.gibibit, Unit.tebibit, Unit.exbibit,
			Unit.zebibit, Unit.yobibit, Unit._byte, Unit.kibibyte, Unit.mebibyte, Unit.gibibyte, Unit.tebibyte,
			Unit.exbibyte, Unit.zebibyte, Unit.yobibyte }),
	DensityUnit(new Unit[] { Unit.kilogramPerCubicMetre, Unit.gramPerCubicMetre }),
	LengthUnit(new Unit[] { Unit.metre, Unit.centimetre, Unit.millimetre, Unit.micrometre, Unit.nanometre,
			Unit.kilometre, Unit.foot, Unit.inch, Unit.mile, Unit.nauticalMile, Unit.astronomicalUnit }),
	ChargeUnit(new Unit[] { Unit.coulomb }),
	EnergyUnit(new Unit[] { Unit.joule, Unit.kilojoule, Unit.megajoule, Unit.gigajoule, Unit.electronvolt,
			Unit.megaelectronvolt, Unit.kilowattHour }),
	ForceUnit(new Unit[] { Unit.newton, Unit.pound, Unit.ounce, Unit.ton }),
	FrequencyUnit(new Unit[] { Unit.hertz, Unit.kilohertz, Unit.megahertz, Unit.gigahertz }),
	IlluminanceUnit(new Unit[] { Unit.lux, Unit.footcandle }),
	InductanceUnit(new Unit[] { Unit.henry, Unit.millihenry, Unit.microhenry }),
	LuminanceUnit(new Unit[] { Unit.candelaPerSquareMetre }),
	PowerUnit(new Unit[] { Unit.watt, Unit.microwatt, Unit.milliwatt, Unit.kilowatt, Unit.megawatt, Unit.gigawatt,
			Unit.horsepower, Unit.kilowattHourPerYear }),
	LuminousFluxUnit(new Unit[] { Unit.lumen }), LuminousIntensityUnit(new Unit[] { Unit.candela }),
	MagneticFluxUnit(new Unit[] { Unit.weber, Unit.maxwell }), MagneticInductionUnit(new Unit[] { Unit.tesla }),
	MassUnit(new Unit[] { Unit.kilogram, Unit.gram, Unit.milligram, Unit.microgram, Unit.tonne, Unit.slug }),
	MassFlowRateUnit(new Unit[] { Unit.gramPerSecond, Unit.kilogramPerSecond, Unit.gramPerHour, Unit.kilogramPerHour }),

	PressureUnit(new Unit[] { Unit.pascal, Unit.kilopascal, Unit.bar, Unit.millibar, Unit.millimetresOfMercury,
			Unit.poundPerSquareInch, Unit.inchesOfMercury, Unit.inchesOfWater }),
	unitless(new Unit[] { Unit.unity, Unit.percent }),
	ResistanceUnit(new Unit[] { Unit.ohm, Unit.milliohm, Unit.kiloohm, Unit.megaohm }),
	SoundPressureUnit(new Unit[] { Unit.decibel, Unit.bel }),
	TemperatureUnit(new Unit[] { Unit.kelvin, Unit.degreeCelsius, Unit.degreeFahrenheit }),
	TimeUnit(new Unit[] { Unit.second, Unit.millisecond, Unit.microsecond, Unit.nanosecond, Unit.minute, Unit.hour,
			Unit.day, Unit.year }),
	TorqueUnit(new Unit[] { Unit.newtonMetre }),
	VelocityUnit(new Unit[] { Unit.metrePerSecond, Unit.centimetrePerSecond, Unit.kilometrePerSecond, Unit.metrePerHour,
			Unit.kilometrePerHour, Unit.milePerHour, Unit.milePerSecond, Unit.knot }),
	VoltageUnit(new Unit[] { Unit.volt, Unit.millivolt, Unit.microvolt, Unit.kilovolt, Unit.megavolt }),
	VolumeUnit(new Unit[] { Unit.cubicMetre, Unit.cubicCentimetre, Unit.litre, Unit.millilitre, Unit.cubicFoot,
			Unit.cubicInch, Unit.fluidOunce, Unit.gallon }),
	VolumeFlowRateUnit(
			new Unit[] { Unit.litrePerSecond, Unit.millilitrePerSecond, Unit.litrePerHour, Unit.millilitrePerHour });

	private Unit[] units;

	private UnitType(Unit[] units) {
		this.units = units;
	}

	public Collection<SemanticType> getSemanticType() {
		final Set<SemanticType> result = new HashSet<>();
		for (final SemanticType s : SemanticType.values()) {
			if (s.getUnitType().equals(this)) {
				result.add(s);
			}
		}
		return result;
	}

	public Unit[] getUnits() {
		return units;
	}

}
