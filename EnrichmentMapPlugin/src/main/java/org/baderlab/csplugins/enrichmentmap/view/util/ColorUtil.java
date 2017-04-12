package org.baderlab.csplugins.enrichmentmap.view.util;

import java.awt.Color;

import org.baderlab.csplugins.enrichmentmap.util.MathUtil;

public final class ColorUtil {

	private static double EPSILON = 1e-30;
	
	private ColorUtil() {}
	
	public static Color getContrastingColor(final Color color) {
		int d = 0;
		// Counting the perceptive luminance - human eye favors green color...
		final double a = 1 - (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;

		if (a < 0.5)
			d = 0; // bright colors - black font
		else
			d = 255; // dark colors - white font

		return new Color(d, d, d);
	}
	
	public static String toHexString(final Color color) {
		final int rgb = color.getRGB();
		final String hex = String.format("#%06X", (0xFFFFFF & rgb));

		return hex;
	}
	
	public static Color parseColor(final String input) {
		Color color = null;
		
		if (input.matches("^#([A-Fa-f0-9]{8}|[A-Fa-f0-9]{6})$")) {
			// We have a hex value with either 6 (rgb) or 8 (rgba) digits
			int r = Integer.parseInt(input.substring(1, 3), 16);
			int g = Integer.parseInt(input.substring(3, 5), 16);
			int b = Integer.parseInt(input.substring(5, 7), 16);
			
			if (input.length() > 7) {
				int a = Integer.parseInt(input.substring(7, 9), 16);
				color = new Color(r, g, b, a);
			} else {
				color = new Color(r, g, b);
			}
		} else {
			// Check for color string
			color = ColorKeyword.getColor(input);
		}
		
		return color;
	}
	
	public static Color getColor(
			final double value,
			final double lowerBound,
			final double upperBound,
			final Color lowerColor,
			final Color zeroColor,
			final Color upperColor
	) {
		final boolean hasZero = lowerBound < -EPSILON && upperBound > EPSILON && zeroColor != null;

		if (hasZero && value < EPSILON && value > -EPSILON)
			return zeroColor;

		final Color color = value < 0.0 ? lowerColor : upperColor;

		// Linearly interpolate the value
		final double f = value < 0.0 ? MathUtil.invLinearInterp(value, lowerBound, 0)
				: MathUtil.invLinearInterp(value, 0, upperBound);
		float t = (float) (value < 0.0 ? MathUtil.linearInterp(f, 0.0, 1.0) : MathUtil.linearInterp(f, 1.0, 0.0));

		// Make sure it's between 0.0-1.0
		t = Math.max(0.0f, t);
		t = Math.min(1.0f, t);

		return ColorUtil.interpolate(zeroColor, color, t);
	}
	
	public static Color interpolate(Color c1, Color c2, float t) {
		float[] comp1 = c1.getRGBComponents(null);
		float[] comp2 = c2.getRGBComponents(null);
		float[] comp3 = new float[4];

		for (int i = 0; i < 4; i++)
			comp3[i] = comp2[i] + (comp1[i] - comp2[i]) * t;

		return new Color(comp3[0], comp3[1], comp3[2], comp3[3]);
	}
}