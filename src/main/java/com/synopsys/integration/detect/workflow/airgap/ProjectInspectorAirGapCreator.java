/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.airgap;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.tool.detector.inspectors.projectinspector.AirgapProjectInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.projectinspector.ArtifactoryProjectInspectorInstaller;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class ProjectInspectorAirGapCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArtifactoryProjectInspectorInstaller projectInspectorInstaller;

    public ProjectInspectorAirGapCreator(ArtifactoryProjectInspectorInstaller projectInspectorInstaller) {
        this.projectInspectorInstaller = projectInspectorInstaller;
    }

    public void installDependencies(File nugetFolder) throws DetectUserFriendlyException {
        logger.info("Installing project inspector for linux.");
        installThenCopy(nugetFolder, AirgapProjectInspectorResolver.LINUX_DIR, ArtifactoryConstants.PROJECT_INSPECTOR_LINUX_PROPERTY);

        logger.info("Installing project inspector for windows.");
        installThenCopy(nugetFolder, AirgapProjectInspectorResolver.WINDOWS_DIR, ArtifactoryConstants.PROJECT_INSPECTOR_WINDOWS_PROPERTY);

        logger.info("Installing project inspector for mac.");
        installThenCopy(nugetFolder, AirgapProjectInspectorResolver.MAC_DIR, ArtifactoryConstants.PROJECT_INSPECTOR_MAC_PROPERTY);
    }

    private void installThenCopy(File container, String targetDirectory, String propertyName) throws DetectUserFriendlyException {
        try {
            File destination = new File(container, targetDirectory);
            File downloaded = projectInspectorInstaller.downloadZip(propertyName, destination);

            FileUtils.copyDirectory(downloaded, destination);
            FileUtils.deleteDirectory(downloaded);
        } catch (DetectableException | IOException e) {
            throw new DetectUserFriendlyException("An error occurred installing project-inspector to the " + targetDirectory + " folder.", e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
