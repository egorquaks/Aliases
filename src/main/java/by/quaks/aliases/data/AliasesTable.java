package by.quaks.aliases.data;

import com.j256.ormlite.field.DatabaseField;
import lombok.*;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor
public class AliasesTable implements ITable {
    @DatabaseField(generatedId = true)
    private long id;
    @NonNull
    @Setter
    @DatabaseField
    private UUID uuid;
    @NonNull
    @Setter
    @DatabaseField
    private String alias;
    @NonNull
    @Setter
    @DatabaseField
    private String source;
}
