import java.util.ArrayList;
import java.util.Arrays;

public class MagicBitboards {
    public static long[] rookMagicNum = {36046527717736448L, 594486283369185728L, 72092778679042114L, 2341876238647300096L, 144150683036418560L, 144132784624501248L, 144117387120117764L, -9079255747115603452L, 80079631947931777L, 18436885860868104L, 604326844254191684L, 72620587478958593L, 281543830470660L, 288371122230263936L, 720717227657003264L, 612771061146198272L, 2296330105913473L, 1271585734410240L, 4684034983583563792L, 289393659726531072L, 45881520849683472L, 108227678368170496L, 74766950733840L, 4755803405535479876L, 1197993237155954691L, 2918367743981932544L, -8926134319710142448L, 2449966995531104384L, 144119588278241280L, 4400202383872L, 4612813020001731072L, 2305843292681568516L, 18014683629879424L, 13511073797775361L, 576601627239124992L, 2360167817308803076L, 2323998299872299008L, 2814770470195208L, 2323937126678269954L, 844701989077252L, 140876001083392L, 585485579196907520L, 4044236932170285088L, -8070167888970842104L, 9015995414904960L, 5836669515152261248L, 2346463920874848296L, 1441152981352448004L, 81487006832667264L, 3413159121552128L, 288511938170716416L, 9149379852632448L, 153685354732855808L, 684551541440413824L, 4685469850289071104L, 148900263787266304L, 6917551294915813890L, 36099166216151937L, 38351103016831233L, 144124052890517589L, 72620681565570050L, 2313161375922327682L, 1153018880239665668L, 579365138591665154L};
    public static long[] bishopMagicNum = {5784908923988361472L, 4541567143149570L, 18309104141500938L, 2308130543708946592L, 299342044861440L, 4900202408333901826L, 171209388504719360L, 5139118439284752L, 2305847476253430032L, 215521661307395L, 4613524472803131664L, 8952876105984L, -8934433557497675760L, 441670694288228352L, 9007758693113856L, 576604789437105349L, 5271472229200757760L, 1208104206565677584L, 307871879528704L, 2387189655981654020L, 1154065005323363328L, 145258723152004098L, 576601494221488704L, 281475580757096L, 13669815904370944L, 1156721397700612L, 4765951898941456416L, 18155685888000002L, -9223090492000944128L, 76561474255867904L, 2308308132067345416L, 1691190785278213L, 6757632833576993L, 2342472277021425922L, 72638720296091716L, 3499332129199423568L, 577595456893231112L, 612683202955378816L, 1153521839029621760L, 2253075968625024L, 4629983578762059809L, 1129371179942434L, 2377913798599418881L, 869221254203179264L, -2300758858689114080L, 163273662827331648L, 108651574430073088L, 2415644228190348L, 2306150881233567745L, 78290731093985280L, 72057886433083392L, -4611684643458511352L, 144115205339874306L, -8536561952128630784L, 18019363500196128L, 37191547897380880L, 4620975001905432586L, -9209860679610064880L, 27033152367887360L, 1153493336554768386L, 90151177248984080L, 4616286512584147458L, 729618633815752777L, 705903714533888L};

    public static long[][] rookAttacksArray = new long[64][4096];
    public static long[][] bishopAttacksArray = new long[64][512];
    public static long[] rookMasks = new long[64];
    public static long[] bishopMasks = new long[64];
    public static final int[] rookSquareAmounts = {12, 11, 11, 11, 11, 11, 11, 12, 11, 10, 10, 10, 10, 10, 10, 11, 11, 10, 10, 10, 10, 10, 10, 11, 11, 10, 10, 10, 10, 10, 10, 11, 11, 10, 10, 10, 10, 10, 10, 11, 11, 10, 10, 10, 10, 10, 10, 11, 11, 10, 10, 10, 10, 10, 10, 11, 12, 11, 11, 11, 11, 11, 11, 12};
    public static final int[] bishopSquareAmounts = {6, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 7, 7, 5, 5, 5, 5, 7, 9, 9, 7, 5, 5, 5, 5, 7, 9, 9, 7, 5, 5, 5, 5, 7, 7, 7, 7, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 6};
    public static int state = 1804289383;

    public static long rookFindMagicNumber(int square, int squareAmount) {
        long[] occupied = new long[4096];
        long[] attacks = new long[4096];
        long[] usedAttacks = new long[4096];
        long attackMask = rookMask(square);
        long occupancyMax = 1L << squareAmount;
        int index, fail;
        for (int i = 0; i < occupancyMax; i++) {
            occupied[i] = setOccupied(i, squareAmount, attackMask);
            attacks[i] = rookAttacks(square, occupied[i]);
        }
        for (int i = 0; i < 10000000; i++) {
            long magicNumber = randomFewBits();
            if ((Evaluation.countBits(attackMask * magicNumber & 0xFF00000000000000L)) < 6) continue;
            Arrays.fill(usedAttacks, 0);
            for (index = 0, fail = 0; fail == 0 && index < occupancyMax; index++) {
                int magicIndex = (int) ((occupied[index] * magicNumber) >>> (64 - squareAmount));
                try {
                    if (usedAttacks[magicIndex] == 0L) {
                        usedAttacks[magicIndex] = attacks[index];
                    } else if (usedAttacks[index] != attacks[index]) {
                        fail = 1;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                    fail = 1;
                }
            }
            if (fail == 0) {
                return magicNumber;
            }
        }
        System.out.println("Magic number failed");
        return 0L;
    }

    public static long bishopFindMagicNumber(int square, int squareAmount) {
        long[] occupied = new long[512];
        long[] attacks = new long[512];
        long[] usedAttacks = new long[512];
        long attackMask = bishopMask(square);
        long occupancyMax = 1L << squareAmount;
        int index, fail;
        for (int i = 0; i < occupancyMax; i++) {
            occupied[i] = setOccupied(i, squareAmount, attackMask);
            attacks[i] = bishopAttacks(square, occupied[i]);
        }
        for (int i = 0; i < 10000000; i++) {
            long magicNumber = randomFewBits();
            if ((Evaluation.countBits(attackMask * magicNumber & 0xFF00000000000000L)) < 6) continue;
            Arrays.fill(usedAttacks, 0);
            for (index = 0, fail = 0; fail == 0 && index < occupancyMax; index++) {
                int magicIndex = (int) ((occupied[index] * magicNumber) >>> (64 - squareAmount));
                try {
                    if (usedAttacks[magicIndex] == 0L) {
                        usedAttacks[magicIndex] = attacks[index];
                    } else if (usedAttacks[index] != attacks[index]) {
                        fail = 1;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                    fail = 1;
                }
            }
            if (fail == 0) {
                return magicNumber;
            }
        }
        System.out.println("Magic number failed");
        return 0L;
    }

    public static void testMagicNumbers() {
        for (int i = 0; i < 64; i++) {
            System.out.printf("%dL,\n", rookFindMagicNumber(i, rookSquareAmounts[i]));
        }
        System.out.println("\n");
        for (int i = 0; i < 64; i++) {
            System.out.printf("%dL,\n", bishopFindMagicNumber(i, bishopSquareAmounts[i]));
        }
    }

    public static void initializeArrays() {
        long rookMask, bishopMask, occupancy, rookOccupancyVariations, bishopOccupancyVariations, magicIndex;
        int rookAmountOfAttackedSquares, bishopAmountOfAttackedSquares;
        for (int i = 0; i < 64; i++) {
            rookMask = rookMask(i);
            rookMasks[i] = rookMask;
            bishopMask = bishopMask(i);
            bishopMasks[i] = bishopMask;
            bishopAmountOfAttackedSquares = Evaluation.countBits(bishopMask);
            rookAmountOfAttackedSquares = Evaluation.countBits(rookMask);
            rookOccupancyVariations = 1L << rookAmountOfAttackedSquares;
            bishopOccupancyVariations = 1L << bishopAmountOfAttackedSquares;
            for (int j = 0; j < rookOccupancyVariations; j++) {
                occupancy = setOccupied(j, rookAmountOfAttackedSquares, rookMask);
                magicIndex = (occupancy * rookMagicNum[i]) >>> (64 - rookSquareAmounts[i]);
                rookAttacksArray[i][(int) magicIndex] = rookAttacks(i, occupancy);
            }
            for (int j = 0; j < bishopOccupancyVariations; j++) {
                occupancy = setOccupied(j, bishopAmountOfAttackedSquares, bishopMask);
                magicIndex = (occupancy * bishopMagicNum[i]) >>> (64 - bishopSquareAmounts[i]);
                bishopAttacksArray[i][(int) magicIndex] = bishopAttacks(i, occupancy);
            }
        }
    }

    public static int generate_random_number() {
        int x = state;
        x ^= x << 13;
        x ^= x >> 17;
        x ^= x << 5;
        state = x;
        return x;
    }

    public static long randomLong() {
        long u1, u2, u3, u4;
        u1 = (long) (generate_random_number()) & 0xFFFF;
        u2 = (long) (generate_random_number()) & 0xFFFF;
        u3 = (long) (generate_random_number()) & 0xFFFF;
        u4 = (long) (generate_random_number()) & 0xFFFF;
        return u1 | (u2 << 16) | (u3 << 32) | (u4 << 48);
    }

    public static long randomFewBits() {
        return randomLong() & randomLong() & randomLong();
    }

    public static long setOccupied(long index, int bitsAmount, long attackMask) {
        long occupied = 0;
        for (int i = 0; i < bitsAmount; i++) {
            if ((index >> i & 1) == 1) {
                occupied |= getSpecificBit(attackMask, i);
            }
        }
        return occupied;
    }

    public static long getSpecificBit(long bits, int bitNum) {
        ArrayList<Long> separatedBits = MoveGeneration.separateBits(bits);
        return separatedBits.get(bitNum);
    }

    public static long rookMask(int square) {
        long attacks = 0L;
        int f, r;
        int tr = square / 8;
        int tf = square % 8;
        for (r = tr + 1; r <= 6; r++) attacks |= (1L << (r * 8 + tf));
        for (r = tr - 1; r >= 1; r--) attacks |= (1L << (r * 8 + tf));
        for (f = tf + 1; f <= 6; f++) attacks |= (1L << (tr * 8 + f));
        for (f = tf - 1; f >= 1; f--) attacks |= (1L << (tr * 8 + f));
        return attacks;
    }

    public static long bishopMask(int square) {
        long attacks = 0L;
        int f, r;
        int tr = square / 8;
        int tf = square % 8;
        for (r = tr + 1, f = tf + 1; r <= 6 && f <= 6; r++, f++) attacks |= (1L << (r * 8 + f));
        for (r = tr + 1, f = tf - 1; r <= 6 && f >= 1; r++, f--) attacks |= (1L << (r * 8 + f));
        for (r = tr - 1, f = tf + 1; r >= 1 && f <= 6; r--, f++) attacks |= (1L << (r * 8 + f));
        for (r = tr - 1, f = tf - 1; r >= 1 && f >= 1; r--, f--) attacks |= (1L << (r * 8 + f));
        return attacks;
    }

    public static long rookAttacks(int piece, long occupancy) {
        long attacks = 0L;
        int f, r, tr = piece / 8, tf = piece % 8;
        for (r = tr + 1; r <= 7; r++) {
            attacks |= (1L << (r * 8 + tf));
            if ((occupancy & (1L << (r * 8 + tf))) != 0) {
                break;
            }
        }

        for (r = tr - 1; r >= 0; r--) {
            attacks |= (1L << (r * 8 + tf));
            if ((occupancy & (1L << (r * 8 + tf))) != 0) {
                break;
            }
        }

        for (f = tf + 1; f <= 7; f++) {
            attacks |= (1L << (tr * 8 + f));
            if ((occupancy & (1L << (tr * 8 + f))) != 0) {
                break;
            }
        }

        for (f = tf - 1; f >= 0; f--) {
            attacks |= (1L << (tr * 8 + f));
            if ((occupancy & (1L << (tr * 8 + f))) != 0) {
                break;
            }
        }
        return attacks;
    }

    public static long bishopAttacks(int piece, long occupancy) {
        long attacks = 0L;
        int f, r, tr = piece / 8, tf = piece % 8;
        for (r = tr + 1, f = tf + 1; r <= 7 && f <= 7; r++, f++) {
            attacks |= (1L << (r * 8 + f));
            if ((occupancy & (1L << (r * 8 + f))) != 0) {
                break;
            }
        }

        for (r = tr + 1, f = tf - 1; r <= 7 && f >= 0; r++, f--) {
            attacks |= (1L << (r * 8 + f));
            if ((occupancy & (1L << (r * 8 + f))) != 0) {
                break;
            }
        }

        for (r = tr - 1, f = tf + 1; r >= 0 && f <= 7; r--, f++) {
            attacks |= (1L << (r * 8 + f));
            if ((occupancy & (1L << (r * 8 + f))) != 0) {
                break;
            }
        }

        for (r = tr - 1, f = tf - 1; r >= 0 && f >= 0; r--, f--) {
            attacks |= (1L << (r * 8 + f));
            if ((occupancy & (1L << (r * 8 + f))) != 0) {
                break;
            }
        }

        return attacks;
    }

    public static long getRookAttacks(int square, long occupied) {
        occupied &= rookMasks[square];
        occupied *= rookMagicNum[square];
        occupied >>>= 64 - rookSquareAmounts[square];
        return rookAttacksArray[square][(int) occupied];
    }

    public static long getBishopAttacks(int square, long occupied) {
        occupied &= bishopMasks[square];
        occupied *= bishopMagicNum[square];
        occupied >>>= 64 - bishopSquareAmounts[square];
        return bishopAttacksArray[square][(int) occupied];
    }
}
