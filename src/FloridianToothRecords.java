/*
   Program: FloridianToothRecords.java

   The program records and manages the dental information for up to six
   members of a Florida family. Each person has upper and lower teeth, and
   each tooth is recorded as either an Incisor (I), Bicuspid (B), or Missing (M).

   The program allows the user to:
   - Print the family’s tooth records
   - Extract (remove) a tooth safely
   - Compute the family’s root canal indices based on a quadratic equation
     I*x^2 + B*x - M = 0  (I, B, M are totals across the family)
   - Exit politely

   The program uses a 3D array: teeth[person][layer][tooth].
   Inputs are validated (case-insensitive). Local variables are declared at
   method starts per CSC120 rules.

   Author:  [Your Name]
   Course:  CSC120
   Date:    27 October 2025
*/

import java.util.Scanner;

/**
 * Class FloridianToothRecords
 * Controls all family dental record operations.
 */
public class FloridianToothRecords
{
    public static final int MAX_PEOPLE = 6;
    public static final int MAX_TEETH  = 8;
    public static final int UPPER      = 0;
    public static final int LOWER      = 1;

    /**
     * Main method controls program flow.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args)
    {
        Scanner sc;
        int numPeople;
        String[] names;
        char[][][] teeth;
        int[][] toothCounts;

        sc = new Scanner(System.in);

        System.out.println("Welcome to the Floridian Tooth Records");
        System.out.println("--------------------------------------");

        numPeople = getNumberOfPeople(sc);
        names = new String[numPeople];
        teeth = new char[numPeople][2][MAX_TEETH];
        toothCounts = new int[numPeople][2];

        inputFamilyData(sc, names, teeth, toothCounts, numPeople);
        menuLoop(sc, names, teeth, toothCounts, numPeople);

        sc.close();
    }

    /**
     * Prompt for and return a valid number of people (1..MAX_PEOPLE).
     * Error re-prompts appear on the same line to match sample behavior.
     *
     * @param sc Scanner for input
     * @return number of people
     */
    public static int getNumberOfPeople(Scanner sc)
    {
        int numPeople;
        numPeople = 0;

        System.out.print("Please enter number of people in the family : ");
        numPeople = sc.nextInt();

        while (numPeople < 1 || numPeople > MAX_PEOPLE)
        {
            System.out.print("Invalid number of people, try again         : ");
            numPeople = sc.nextInt();
        }

        sc.nextLine(); // consume remainder of line
        return numPeople;
    }

    /**
     * Input names and teeth strings for each family member.
     * Validates teeth strings and stores uppercase characters in teeth array.
     *
     * Error re-prompts (invalid types / too many teeth) re-ask on the same line.
     *
     * @param sc Scanner for input
     * @param names array to store names
     * @param teeth 3D char array to store tooth letters
     * @param toothCounts counts per person per layer
     * @param numPeople number of people in family
     */
    public static void inputFamilyData(Scanner sc, String[] names, char[][][] teeth,
                                       int[][] toothCounts, int numPeople)
    {
        int personIndex;
        personIndex = 0;

        while (personIndex < numPeople)
        {
            String name;
            String upperString;
            String lowerString;
            int i;
            int j;

            System.out.print("Please enter the name for family member " + (personIndex + 1)
                    + "   : ");
            name = sc.nextLine().trim();
            names[personIndex] = name;

            // Uppers: initial prompt, then same-line re-prompts if invalid
            System.out.print("Please enter the uppers for " + name + "       : ");
            upperString = sc.nextLine().trim();
            while (true)
            {
                if (!isValidTeethString(upperString))
                {
                    System.out.print("Invalid teeth types, try again              : ");
                    upperString = sc.nextLine().trim();
                    continue;
                }
                if (upperString.length() > MAX_TEETH)
                {
                    System.out.print("Too many teeth, try again                   : ");
                    upperString = sc.nextLine().trim();
                    continue;
                }
                break;
            }

            // Lowers: initial prompt, then same-line re-prompts if invalid
            System.out.print("Please enter the lowers for " + name + "       : ");
            lowerString = sc.nextLine().trim();
            while (true)
            {
                if (!isValidTeethString(lowerString))
                {
                    System.out.print("Invalid teeth types, try again              : ");
                    lowerString = sc.nextLine().trim();
                    continue;
                }
                if (lowerString.length() > MAX_TEETH)
                {
                    System.out.print("Too many teeth, try again                   : ");
                    lowerString = sc.nextLine().trim();
                    continue;
                }
                break;
            }

            // Store upper letters (pad remaining with '\0')
            for (i = 0; i < MAX_TEETH; i = i + 1)
            {
                teeth[personIndex][UPPER][i] = '\0';
            }
            for (i = 0; i < upperString.length(); i = i + 1)
            {
                teeth[personIndex][UPPER][i] = Character.toUpperCase(upperString.charAt(i));
            }
            toothCounts[personIndex][UPPER] = upperString.length();

            // Store lower letters (pad remaining with '\0')
            for (j = 0; j < MAX_TEETH; j = j + 1)
            {
                teeth[personIndex][LOWER][j] = '\0';
            }
            for (j = 0; j < lowerString.length(); j = j + 1)
            {
                teeth[personIndex][LOWER][j] = Character.toUpperCase(lowerString.charAt(j));
            }
            toothCounts[personIndex][LOWER] = lowerString.length();

            personIndex = personIndex + 1;
        }
    }

    /**
     * Validate a teeth string: must contain only letters I, B, or M (any case).
     *
     * @param s input string
     * @return true if valid, false otherwise
     */
    public static boolean isValidTeethString(String s)
    {
        int idx;
        idx = 0;

        if (s.length() == 0)
        {
            return false;
        }

        while (idx < s.length())
        {
            char c;
            c = Character.toUpperCase(s.charAt(idx));
            if (c != 'I' && c != 'B' && c != 'M')
            {
                return false;
            }
            idx = idx + 1;
        }
        return true;
    }

    /**
     * Main menu loop. Accepts P/E/R/X options (case-insensitive) and dispatches.
     *
     * Error re-prompts for invalid menu option appear on same line.
     *
     * @param sc Scanner for input
     * @param names array of names
     * @param teeth 3D teeth array
     * @param toothCounts counts per person per layer
     * @param numPeople number of people
     */
    public static void menuLoop(Scanner sc, String[] names, char[][][] teeth,
                                int[][] toothCounts, int numPeople)
    {
        String choice;
        choice = "";

        while (true)
        {
            System.out.print("\n(P)rint, (E)xtract, (R)oot, e(X)it          : ");
            choice = sc.nextLine().trim();
            // If user enters empty, re-prompt inline
            while (choice.length() == 0)
            {
                System.out.print("Invalid menu option, try again              : ");
                choice = sc.nextLine().trim();
            }
            char opt;
            opt = Character.toUpperCase(choice.charAt(0));

            if (opt == 'P')
            {
                printFamilyRecords(names, teeth, toothCounts, numPeople);
            }
            else if (opt == 'E')
            {
                extractTooth(sc, names, teeth, toothCounts, numPeople);
            }
            else if (opt == 'R')
            {
                computeRootCanalIndices(teeth, toothCounts, numPeople);
            }
            else if (opt == 'X')
            {
                System.out.println("\nExiting the Floridian Tooth Records :-)");
                break;
            }
            else
            {
                // Invalid option: re-ask on same line matched to sample run
                System.out.print("Invalid menu option, try again              : ");
                choice = sc.nextLine().trim();
                // At this point, we'll go to top of loop to process choice
                // but to avoid printing the full menu again immediately, handle inline:
                opt = (choice.length() > 0) ? Character.toUpperCase(choice.charAt(0)) : '\0';
                if (opt == 'P')
                {
                    printFamilyRecords(names, teeth, toothCounts, numPeople);
                }
                else if (opt == 'E')
                {
                    extractTooth(sc, names, teeth, toothCounts, numPeople);
                }
                else if (opt == 'R')
                {
                    computeRootCanalIndices(teeth, toothCounts, numPeople);
                }
                else if (opt == 'X')
                {
                    System.out.println("\nExiting the Floridian Tooth Records :-)");
                    break;
                }
                // otherwise loop continues and full menu will be shown again
            }
        }
    }

    /**
     * Print the family's teeth records in a readable format.
     *
     * @param names array of names
     * @param teeth 3D teeth array
     * @param toothCounts counts per person per layer
     * @param numPeople number of people
     */
    public static void printFamilyRecords(String[] names, char[][][] teeth,
                                          int[][] toothCounts, int numPeople)
    {
        int personIndex;
        personIndex = 0;

        System.out.println();

        while (personIndex < numPeople)
        {
            int layer;
            int toothIdx;

            System.out.println(names[personIndex]);

            // Uppers
            System.out.print("  Uppers:  ");
            layer = UPPER;
            toothIdx = 0;
            while (toothIdx < toothCounts[personIndex][layer])
            {
                System.out.print((toothIdx + 1) + ":" + teeth[personIndex][layer][toothIdx]);
                if (toothIdx < toothCounts[personIndex][layer] - 1)
                {
                    System.out.print("  ");
                }
                toothIdx = toothIdx + 1;
            }
            System.out.println();

            // Lowers
            System.out.print("  Lowers:  ");
            layer = LOWER;
            toothIdx = 0;
            while (toothIdx < toothCounts[personIndex][layer])
            {
                System.out.print((toothIdx + 1) + ":" + teeth[personIndex][layer][toothIdx]);
                if (toothIdx < toothCounts[personIndex][layer] - 1)
                {
                    System.out.print("  ");
                }
                toothIdx = toothIdx + 1;
            }
            System.out.println();

            personIndex = personIndex + 1;
        }
    }

    /**
     * Extract (remove) a tooth: asks for family member, layer (U/L), tooth number.
     * Validates entries and ensures the tooth is not already missing before setting it to 'M'.
     *
     * Error re-prompts are printed on the same line (invalid family member, invalid layer,
     * invalid tooth number, missing tooth).
     *
     * @param sc Scanner for input
     * @param names array of names
     * @param teeth 3D teeth array
     * @param toothCounts counts per person per layer
     * @param numPeople number of people
     */
    public static void extractTooth(Scanner sc, String[] names, char[][][] teeth,
                                    int[][] toothCounts, int numPeople)
    {
        String memberName;
        int personIndex;
        String layerInput;
        char layerChar;
        int layer;
        int toothNumber;

        personIndex = -1;

        // Ask for family member (case-insensitive match), re-prompt same-line if invalid
        System.out.print("Which family member                         : ");
        memberName = sc.nextLine().trim();
        personIndex = findPersonIndex(names, memberName, numPeople);
        while (personIndex == -1)
        {
            System.out.print("Invalid family member, try again            : ");
            memberName = sc.nextLine().trim();
            personIndex = findPersonIndex(names, memberName, numPeople);
        }

        // Ask for layer, same-line re-prompts if invalid
        System.out.print("Which tooth layer (U)pper or (L)ower        : ");
        layerInput = sc.nextLine().trim();
        while (true)
        {
            if (layerInput.length() == 0)
            {
                System.out.print("Invalid layer, try again                    : ");
                layerInput = sc.nextLine().trim();
                continue;
            }
            layerChar = Character.toUpperCase(layerInput.charAt(0));
            if (layerChar == 'U')
            {
                layer = UPPER;
                break;
            }
            else if (layerChar == 'L')
            {
                layer = LOWER;
                break;
            }
            else
            {
                System.out.print("Invalid layer, try again                    : ");
                layerInput = sc.nextLine().trim();
            }
        }

        // Ask for tooth number; re-prompt same-line for invalid numbers or if beyond count
        System.out.print("Which tooth number                          : ");
        while (true)
        {
            if (!sc.hasNextInt())
            {
                // if the user typed non-number, consume it and re-ask inline
                sc.nextLine();
                System.out.print("Invalid tooth number, try again             : ");
                continue;
            }
            toothNumber = sc.nextInt();
            sc.nextLine(); // consume newline
            if (toothNumber < 1 || toothNumber > MAX_TEETH)
            {
                System.out.print("Invalid tooth number, try again             : ");
                continue;
            }
            if (toothNumber > toothCounts[personIndex][layer])
            {
                System.out.print("Invalid tooth number, try again             : ");
                continue;
            }
            // Check if it's already missing
            char current;
            current = teeth[personIndex][layer][toothNumber - 1];
            if (current == 'M')
            {
                System.out.print("Missing tooth, try again                    : ");
                continue;
            }
            // valid, perform extraction
            teeth[personIndex][layer][toothNumber - 1] = 'M';
            break;
        }
    }

    /**
     * Find the index of a person by name (case-insensitive).
     *
     * @param names array of names
     * @param query queried name
     * @param numPeople number of people
     * @return index or -1 if not found
     */
    public static int findPersonIndex(String[] names, String query, int numPeople)
    {
        int i;
        i = 0;
        String lowerQuery;
        lowerQuery = query.toLowerCase();

        while (i < numPeople)
        {
            if (names[i].toLowerCase().equals(lowerQuery))
            {
                return i;
            }
            i = i + 1;
        }
        return -1;
    }

    /**
     * Compute the family's root canal indices: roots of I*x^2 + B*x - M = 0,
     * where I, B, M are totals across all people and layers.
     *
     * @param teeth 3D teeth array
     * @param toothCounts counts per person per layer
     * @param numPeople number of people
     */
    public static void computeRootCanalIndices(char[][][] teeth, int[][] toothCounts,
                                               int numPeople)
    {
        int I;
        int B;
        int M;
        int personIndex;
        int layer;
        int t;
        I = 0;
        B = 0;
        M = 0;
        personIndex = 0;

        while (personIndex < numPeople)
        {
            layer = UPPER;
            while (layer <= LOWER)
            {
                t = 0;
                while (t < toothCounts[personIndex][layer])
                {
                    char c;
                    c = teeth[personIndex][layer][t];
                    if (c == 'I')
                    {
                        I = I + 1;
                    }
                    else if (c == 'B')
                    {
                        B = B + 1;
                    }
                    else if (c == 'M')
                    {
                        M = M + 1;
                    }
                    t = t + 1;
                }
                layer = layer + 1;
            }
            personIndex = personIndex + 1;
        }

        // Solve I*x^2 + B*x - M = 0
        System.out.println();
        if (I == 0)
        {
            if (B == 0)
            {
                if (M == 0)
                {
                    System.out.println("Infinite number of root canals (all coefficients zero).");
                }
                else
                {
                    System.out.println("No root canals (equation is -" + M + " = 0).");
                }
            }
            else
            {
                // Linear solution B*x - M = 0 -> x = M/B
                double x;
                x = (double) M / (double) B;
                System.out.printf("One root canal at     %.2f%n", x);
            }
            return;
        }

        // Quadratic solution
        double discriminant;
        double root1;
        double root2;
        discriminant = (double) B * (double) B + 4.0 * (double) I * (double) M;

        if (discriminant < 0.0)
        {
            System.out.println("No real root canals (discriminant < 0).");
            return;
        }

        root1 = ((double) -B + Math.sqrt(discriminant)) / (2.0 * (double) I);
        root2 = ((double) -B - Math.sqrt(discriminant)) / (2.0 * (double) I);

        System.out.printf("One root canal at     %.2f%n", root1);
        System.out.printf("Another root canal at %.2f%n", root2);
    }
}
