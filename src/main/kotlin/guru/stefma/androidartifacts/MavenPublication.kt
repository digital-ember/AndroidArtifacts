package guru.stefma.androidartifacts

import com.android.build.gradle.api.LibraryVariant
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.maven.MavenArtifact
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskContainer

/**
 * Creates a new [MavenArtifact] by putting the generated `aar` file path into it
 * and declare the [MavenArtifact.builtBy] by the generated `assemble$VariantName`
 */
internal fun MavenPublication.addAarArtifact(
        project: Project,
        variantName: String
) {
    artifact("${project.buildDir}/outputs/aar/${variantName.aarFileName(project.name)}") {
        it.builtBy(project.tasks.getByName(variantName.aarAssembleTaskName))
    }
}

/**
 * Creates a new [MavenArtifact] by putting the [TaskContainer.createAndroidArtifactsSourcesTask] into it.
 */
internal fun MavenPublication.addAndroidSourcesArtifact(
        project: Project,
        variant: LibraryVariant
) {
    artifact(project.tasks.createAndroidArtifactsSourcesTask(variant)) {
        it.classifier = "sources"
    }
}

/**
 * Creates a new [MavenArtifact] by putting the [TaskContainer.createJavaArtifactsSourcesTask] into it.
 */
internal fun MavenPublication.addJavaSourcesArtifact(
        project: Project,
        publicationName: String
) {
    val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
    artifact(project.tasks.createJavaArtifactsSourcesTask(javaConvention, publicationName)) {
        it.classifier = "sources"
    }
}

/**
 * Creates a new [MavenArtifact] by putting the [TaskContainer.createAndroidArtifactsJavadocTask] into it.
 */
internal fun MavenPublication.addAndroidJavadocArtifact(
        project: Project,
        variant: LibraryVariant
) {
    artifact(project.tasks.createAndroidArtifactsJavadocTask(project, variant)) {
        it.classifier = "javadoc"
    }
}

/**
 * Creates a new [MavenArtifact] by putting the [TaskContainer.createJavaArtifactsJavadocTask] into it.
 */
internal fun MavenPublication.addJavaJavadocArtifact(
        project: Project,
        publicationName: String
) {
    artifact(project.tasks.createJavaArtifactsJavadocTask(publicationName)) {
        it.classifier = "javadoc"
    }
}

/**
 * Creates a new [MavenArtifact] by putting the [TaskContainer.createAndroidArtifactsDokkaTask] into it.
 */
internal fun MavenPublication.addDokkaArtifact(
        project: Project,
        variant: LibraryVariant
) {
    artifact(project.tasks.createAndroidArtifactsDokkaTask(variant.name)) {
        // TODO: Think avout the classifier.
        // Should be maybe just javadoc and replaced the original javadoc
        // if we use kotlin...
        it.classifier = "${variant.name}Dokka"
    }
}

/**
 * Setup the "metadata" for this [MavenPublication].
 *
 * Currently it will setup the [MavenPublication.setVersion], [MavenPublication.setArtifactId] and
 * the [MavenPublication.setGroupId] based on the [ArtifactsExtension.artifactId], [Project.getVersion] and
 * [Project.getGroup]
 */
internal fun MavenPublication.setupMetadata(
        project: Project,
        extension: ArtifactsExtension
) {
    version = project.version as String
    artifactId = extension.artifactId
    groupId = project.group as String
}

/**
 * Creates the aar file name based on **this** [variantName].
 *
 * The variant name is either something simple like `debug` or `release or something more complex
 * like `flavorDebug` or `paidRelease`.
 *
 * @return a valid aar name which will be generated by the `assemble$variantName` task.
 */
private fun String.aarFileName(baseName: String): String {
    val variantNames = split(Regex("(?<!^)(?=[A-Z])"))
    return buildString {
        append(baseName)
        variantNames.forEach { append("-${it.toLowerCase()}") }
        append(".aar")
    }
}

/**
 * Creates the `assemble` task name for **this** [variantName].
 *
 * The APG (Android Gradle Plugin) creates for each **variantName**  a resp. `assemble$VariantName` tasks.
 * This will returned here.
 */
private val String.aarAssembleTaskName
    get() = "assemble${this.capitalize()}"