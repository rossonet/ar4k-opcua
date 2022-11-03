package org.rossonet.opcua.milo.utils.dtdl;

public enum SemanticType {
	Acceleration(UnitType.AccelerationUnit), Angle(UnitType.AngleUnit),
	AngularAcceleration(UnitType.AngularAccelerationUnit), AngularVelocity(UnitType.AngularVelocityUnit),
	Area(UnitType.AreaUnit), Capacitance(UnitType.CapacitanceUnit), Current(UnitType.CurrentUnit),
	DataRate(UnitType.DataRateUnit), DataSize(UnitType.DataSizeUnit), Density(UnitType.DensityUnit),
	Distance(UnitType.LengthUnit), ElectricCharge(UnitType.ChargeUnit), Energy(UnitType.EnergyUnit),
	Force(UnitType.ForceUnit), Frequency(UnitType.FrequencyUnit), Humidity(UnitType.DensityUnit),
	Illuminance(UnitType.IlluminanceUnit), Inductance(UnitType.InductanceUnit), Latitude(UnitType.AngleUnit),
	Longitude(UnitType.AngleUnit), Length(UnitType.LengthUnit), Luminance(UnitType.LuminanceUnit),
	Luminosity(UnitType.PowerUnit), LuminousFlux(UnitType.LuminousFluxUnit),
	LuminousIntensity(UnitType.LuminousIntensityUnit), MagneticFlux(UnitType.MagneticFluxUnit),
	MagneticInduction(UnitType.MagneticInductionUnit), Mass(UnitType.MassUnit), MassFlowRate(UnitType.MassFlowRateUnit),
	Power(UnitType.PowerUnit), Pressure(UnitType.PressureUnit), RelativeHumidity(UnitType.unitless),
	Resistance(UnitType.ResistanceUnit), SoundPressure(UnitType.SoundPressureUnit),
	Temperature(UnitType.TemperatureUnit), Thrust(UnitType.ForceUnit), TimeSpan(UnitType.TimeUnit),
	Torque(UnitType.TorqueUnit), Velocity(UnitType.VelocityUnit), Voltage(UnitType.VoltageUnit),
	Volume(UnitType.VolumeUnit), VolumeFlowRate(UnitType.VolumeFlowRateUnit);

	private UnitType unitType;

	private SemanticType(UnitType unitType) {
		this.unitType = unitType;
	}

	public UnitType getUnitType() {
		return unitType;
	}
}
