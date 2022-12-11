package me.havooc.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class Day {

    private final Date date;
    private final Block[] blocks;
    private final String groupCode;

    public Day(Block[] blocks, String groupCode) {
        this.groupCode = groupCode;
        final String block_0date = blocks[0].getDate();
        if(!Arrays.stream(blocks).allMatch(x -> Objects.equals(x.getDate(), block_0date)))
            throw new IllegalArgumentException("Co najmniej jeden z bloków jest innej daty niż początkowy, każdy blok powinien mieć datę " + block_0date + ". Dane podanych bloków: " + Utils.getBlockToString(blocks));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        try {
            this.date = sdf.parse(block_0date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Data bloku nieprawidłowa. " + block_0date);
        }
        Block[] b = new Block[7];
        for (int i = 0; i < 7; i++) {
            int finalI = i+1;
            if (Arrays.stream(blocks).anyMatch(x->x.getId() == finalI))
                b[i] = Arrays.stream(blocks).filter(x -> x.getId() == finalI).findFirst().get();
            else
                b[i] = new Block(i+1, block_0date);
        }
        this.blocks = b;
    }

    public boolean isBlockEqual(Day d, int blockId) {
        if (d == null)
            return false;
        return this.getBlock(blockId).equals(d.getBlock(blockId));
    }

    /**
     * Porównuje ze sobą wszystkie bloki
     * @param days Lista planów danego dnia grup do porównania
     * @throws BlockCollideException w przypadku znalezienia kolizji bloków
     */
    public static void isColliding(Day[] days) throws BlockCollideException {
        isColliding(days,-1);
    }

    public Block[] getBlocks() {
        return blocks;
    }

    public static void isColliding(Day[] days, int id) throws BlockCollideException {
        String error = "";
        for (int i = 0; i <days.length; i++) {
            for (int j = 0; j < days.length; j++) {
                if (i<=j)
                    continue;
                if (id<0)
                    for (int k = 1; k <= 7; k++) {
                        if (!days[i].isBlockEqual(days[j], k))
                            error += "Blok nr " + k + " koliduje dla grupy " + days[j].getGroupCode() + " i " + days[i].getGroupCode() + " znaleziono różnice: " + Block.getDifference(days[j].getBlock(k), days[i].getBlock(k)) + "\n";
                    }
                else {
                    if (!days[i].isBlockEqual(days[j], id))
                        error += "Blok nr " + id + " koliduje dla grupy " + days[j].getGroupCode() + " i " + days[i].getGroupCode() + " znaleziono różnice: " + Block.getDifference(days[j].getBlock(id), days[i].getBlock(id)) + "\n";
                }
            }
        }
        if(!error.isEmpty())
            throw new BlockCollideException(error);
    }
    /**
     * @param block_id Numer szukanego bloku
     * @return Zwraca instancję bloku o wskazanym
     */
    public Block getBlock(int block_id) {
        return Arrays.stream(blocks).anyMatch(x->x.getId()==block_id) ? Arrays.stream(blocks).filter(x->x.getId()==block_id).findFirst().get() : null;
    }

    /**
     * @return Zwraca datę w formacie YYYY_MM_DD, kiedy odbywa się blok
     */
    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        return sdf.format(date);
    }

    public String getGroupCode() {
        return groupCode;
    }
}
