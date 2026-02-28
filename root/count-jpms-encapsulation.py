#!/usr/bin/env python3
"""
Count JPMS encapsulation metrics across all modules.
Shows how many public classes are hidden by JPMS module system.
"""

import os
import re
from pathlib import Path
from collections import defaultdict

def find_module_info_files(root_dir):
    """Find all module-info.java files."""
    module_infos = []
    for root, dirs, files in os.walk(root_dir):
        if 'module-info.java' in files:
            module_info_path = os.path.join(root, 'module-info.java')
            module_infos.append(module_info_path)
    return module_infos

def parse_module_info(file_path):
    """Extract module name and exported packages from module-info.java."""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    module_match = re.search(r'module\s+([\w.]+)', content)
    module_name = module_match.group(1) if module_match else None
    
    exports = []
    # Find all exports (both regular and qualified)
    for match in re.finditer(r'exports\s+([\w.]+)(?:\s+to\s+[^;]+)?;', content):
        exports.append(match.group(1))
    
    return module_name, exports

def find_java_files(src_main_java):
    """Find all .java files in src/main/java."""
    java_files = []
    if not os.path.exists(src_main_java):
        return java_files
    
    for root, dirs, files in os.walk(src_main_java):
        for file in files:
            if file.endswith('.java') and file != 'module-info.java' and file != 'package-info.java':
                java_files.append(os.path.join(root, file))
    return java_files

def is_public_type(file_path):
    """Check if the file contains a public class/interface/enum/record."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        # Match public or abstract public types
        return bool(re.search(r'^\s*(public|abstract\s+public|public\s+abstract)\s+(class|interface|enum|record)\s+', content, re.MULTILINE))
    except:
        return False

def get_package_from_file(file_path, src_main_java):
    """Extract package name from Java file path."""
    rel_path = os.path.relpath(file_path, src_main_java)
    package_path = os.path.dirname(rel_path)
    package = package_path.replace(os.sep, '.')
    return package

def analyze_module(module_info_path):
    """Analyze a single module."""
    module_name, exported_packages = parse_module_info(module_info_path)
    
    module_dir = os.path.dirname(module_info_path)
    src_main_java = module_dir
    
    # Find all Java files
    java_files = find_java_files(src_main_java)
    
    public_in_exported = 0
    public_in_hidden = 0
    hidden_packages = set()
    
    for java_file in java_files:
        if is_public_type(java_file):
            package = get_package_from_file(java_file, src_main_java)
            
            # Check if this EXACT package is exported
            is_exported = package in exported_packages
            
            if is_exported:
                public_in_exported += 1
            else:
                public_in_hidden += 1
                hidden_packages.add(package)
    
    return {
        'module': module_name,
        'exported_packages': len(exported_packages),
        'public_in_exported': public_in_exported,
        'public_in_hidden': public_in_hidden,
        'hidden_packages': hidden_packages,
        'module_info_path': module_info_path
    }

def main():
    root_dir = '/home/r-uu/develop/github/main/root'
    
    print("JPMS Encapsulation Analysis")
    print("=" * 80)
    print()
    
    module_infos = find_module_info_files(root_dir)
    
    total_modules = 0
    total_exported_packages = 0
    total_public_exported = 0
    total_public_hidden = 0
    modules_with_hidden = []
    
    for module_info_path in sorted(module_infos):
        stats = analyze_module(module_info_path)
        
        if stats['module']:
            total_modules += 1
            total_exported_packages += stats['exported_packages']
            total_public_exported += stats['public_in_exported']
            total_public_hidden += stats['public_in_hidden']
            
            if stats['public_in_hidden'] > 0:
                modules_with_hidden.append(stats)
    
    # Print summary
    print(f"Total modules analyzed: {total_modules}")
    print(f"Total exported packages: {total_exported_packages}")
    print(f"Total public types in exported packages: {total_public_exported}")
    print(f"Total PUBLIC types in HIDDEN packages: {total_public_hidden}")
    print()
    print(f"Modules with hidden public classes: {len(modules_with_hidden)}")
    print()
    
    # Print modules with hidden classes
    if modules_with_hidden:
        print("Modules with hidden public classes:")
        print("-" * 80)
        for stats in sorted(modules_with_hidden, key=lambda x: x['public_in_hidden'], reverse=True):
            print(f"{stats['module']}: {stats['public_in_hidden']} hidden public classes")
            if stats['hidden_packages']:
                for pkg in sorted(stats['hidden_packages']):
                    print(f"  - {pkg}")
        print()
    
    # Calculate encapsulation ratio
    if total_public_exported + total_public_hidden > 0:
        encapsulation_ratio = (total_public_hidden / (total_public_exported + total_public_hidden)) * 100
        print(f"Encapsulation ratio: {encapsulation_ratio:.1f}% of public types are hidden by JPMS")

if __name__ == '__main__':
    main()
