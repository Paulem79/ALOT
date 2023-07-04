package fr.paulem.api.libs.radios;

import fr.paulem.api.libs.enums.VersionMethod;

public record LibVersion(int major, int minor, int revision) {

    /**
     * 1.19.4 -> 1
     *
     * @return the major
     */
    @Override
    public int major() {
        return major;
    }

    /**
     * 1.19.4 -> 19
     *
     * @return the minor
     */
    @Override
    public int minor() {
        return minor;
    }

    /**
     * 1.19.4 -> 4
     *
     * @return the revision
     */
    @Override
    public int revision() {
        return revision;
    }

    public static LibVersion getVersion(VersionMethod versionMethod) {
        int major, minor, revision;

        if (versionMethod == VersionMethod.BUKKIT) {
            String version = VersionMethod.BUKKIT.getVersion();
            String[] parts = version.split("-")[0].split("\\.");

            major = Integer.parseInt(parts[0]);
            minor = Integer.parseInt(parts[1]);
            revision = Integer.parseInt(parts[2]);
        } else if (versionMethod == VersionMethod.SERVER) {
            String version = VersionMethod.SERVER.getVersion();

            // Extraire la version de Minecraft
            String mcVersion = version.substring(version.indexOf("MC: ") + 4, version.length() - 1);
            String[] mcParts = mcVersion.split("\\.");

            major = Integer.parseInt(mcParts[0]);
            minor = Integer.parseInt(mcParts[1]);
            revision = Integer.parseInt(mcParts[2]);
        } else throw new IllegalArgumentException("Invalid VersionMethod enum value");
        return new LibVersion(major, minor, revision);
    }
}
