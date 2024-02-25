package enhancedportals.utility;

import li.cil.oc.api.machine.Arguments;
import cpw.mods.fml.common.Optional.Method;
import enhancedportals.portal.GlyphIdentifier;

public class ComputerUtils {
    @Method(modid = "OpenComputersAPI|Machine")
    public static Object[] argsToArray(Arguments args) {
        Object[] data = new Object[args.count()];

        for (int i = 0; i < data.length; i++)
            if (args.isString(i))
                data[i] = args.checkString(i);
            else
                data[i] = args.checkAny(i);

        return data;
    }

    public static String verifyGlyphArguments(String s) {
        if (s.length() == 0)
            return "Glyph ID is not formatted correctly";

        if (s.contains(GlyphIdentifier.GLYPH_SEPERATOR)) {
            String[] nums = s.split(GlyphIdentifier.GLYPH_SEPERATOR);

            if (nums.length > 9)
                return "Glyph ID is too long. Must be a maximum of 9 IDs";

            for (String num : nums) {

                int n = Integer.parseInt(num);

                if (n < 0 || n > 27)
                    return "Glyph ID must be between 0 and 27 (inclusive)";
            }
        } else {
            int n = Integer.parseInt(s);

            if (n < 0 || n > 27)
                return "Glyph ID must be between 0 and 27 (inclusive)";
        }

        return null; // All OK
    }
}
