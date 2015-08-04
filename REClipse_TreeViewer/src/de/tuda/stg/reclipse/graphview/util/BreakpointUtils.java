package de.tuda.stg.reclipse.graphview.util;

import de.tuda.stg.reclipse.logger.BreakpointInformation;

import de.tuda.stg.reclipse.graphview.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.IJavaWatchpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;

public class BreakpointUtils {

  private BreakpointUtils() {
  }

  public static IJavaLineBreakpoint createBreakoint(final BreakpointInformation information) {
    final IFile file = findFile(information.getSourcePath());

    if (file == null) {
      return null;
    }

    try {
      return JDIDebugModel.createLineBreakpoint(file, information.getClassName(), information.getLineNumber(), -1, -1, 0, true, null);
    }
    catch (final CoreException e) {
      Activator.log(e);
      return null;
    }
  }

  public static IJavaWatchpoint createWatchpoint(final BreakpointInformation information, final String variableName) {
    final IType type = createTypeFromClassName(information.getClassName());
    final IFile file = findFile(information.getSourcePath());

    if (type == null || file == null) {
      return null;
    }

    try {
      return JDIDebugModel.createWatchpoint(file, type.getFullyQualifiedName(), variableName, -1, -1, -1, 0, true, null);
    }
    catch (final CoreException e) {
      Activator.log(e);
      return null;
    }
  }

  /**
   * Based on the current projects and a class name, generates an appropriate
   * type.
   *
   * @param className
   *          A class name.
   * @return A type.
   */
  private static IType createTypeFromClassName(final String className) {
    // Get projects in current workspace
    final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

    IType resource = null;
    for (final IProject project : projects) {
      final IJavaProject javaProject = JavaCore.create(project);

      IType type = null;
      try {
        type = javaProject.findType(className);
      }
      catch (final JavaModelException e) {
        Activator.log(e);
      }

      if (type != null) {
        resource = type;
        break;
      }
    }
    return resource;
  }

  /**
   * Based on a file name, finds the appropriate file instance.
   *
   * @param fileName
   *          A file name.
   * @return An IFile instance.
   */
  public static IFile findFile(final String fileName) {
    // Get projects in current workspace
    final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

    for (final IProject project : projects) {
      final IFile file = project.getFile(fileName);

      if (file != null && file.exists()) {
        return file;
      }
    }

    return null;
  }

}
