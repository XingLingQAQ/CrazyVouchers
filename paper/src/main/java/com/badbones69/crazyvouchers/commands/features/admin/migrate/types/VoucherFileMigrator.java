package com.badbones69.crazyvouchers.commands.features.admin.migrate.types;

import com.badbones69.crazyvouchers.api.enums.FileSystem;
import com.badbones69.crazyvouchers.commands.features.admin.migrate.IVoucherMigrator;
import com.badbones69.crazyvouchers.commands.features.admin.migrate.enums.MigrationType;
import com.badbones69.crazyvouchers.config.types.ConfigKeys;
import com.ryderbelserion.fusion.core.api.enums.FileType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class VoucherFileMigrator extends IVoucherMigrator {

    public VoucherFileMigrator(@NotNull final CommandSender sender, @NotNull final MigrationType type) {
        super(sender, type);
    }

    @Override
    public void run() {
        switch (this.type) {
            case VOUCHERS_RENAME -> { // rename voucher-codes.yml to codes.yml
                final Path oldPath = this.dataPath.resolve("voucher-codes.yml");

                if (!Files.exists(oldPath)) return;

                final Path newPath = this.dataPath.resolve("codes.yml");

                if (Files.exists(newPath)) {
                    final Path backup = this.dataPath.resolve("backups");

                    if (!Files.exists(backup)) {
                        try {
                            Files.createDirectory(backup);
                        } catch (final IOException exception) {
                            exception.printStackTrace();
                        }
                    }

                    try {
                        Files.move(backup, newPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (final IOException exception) {
                        exception.printStackTrace();
                    }
                }

                try {
                    Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (final Exception exception) {
                    exception.printStackTrace();
                }

                if (this.config.getProperty(ConfigKeys.file_system) == FileSystem.SINGLE) {
                    this.fileManager.removeFile(oldPath, null);
                    this.fileManager.addFile(newPath, FileType.PAPER, new ArrayList<>(), null);

                    this.crazyManager.loadCodes();
                }
            }
        }
    }
}