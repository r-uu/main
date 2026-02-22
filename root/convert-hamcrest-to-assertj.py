#!/usr/bin/env python3
"""
Convert Hamcrest assertions to AssertJ in Java test files
"""

import os
import re
import sys

def convert_file(filepath):
    """Convert Hamcrest assertions to AssertJ in a single file"""
    print(f"Processing: {filepath}")

    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()

        original = content

        # Remove Hamcrest imports
        content = re.sub(r'import static org\.hamcrest\..*?;\n', '', content)
        content = re.sub(r'import org\.hamcrest\..*?;\n', '', content)

        # Replace assertThat(value).isEqualTo(true) with assertThat(value).isTrue()
        content = re.sub(r'\.isEqualTo\(true\)', '.isTrue()', content)
        content = re.sub(r'\.isEqualTo\(false\)', '.isFalse()', content)

        # Replace assertThat(a, is(b)) -> assertThat(a).isEqualTo(b)
        content = re.sub(r'assertThat\(([^,]+),\s*is\(([^)]+)\)\)', r'assertThat(\1).isEqualTo(\2)', content)

        # Replace assertThat(a, notNullValue()) -> assertThat(a).isNotNull()
        content = re.sub(r'assertThat\(([^,]+),\s*notNullValue\(\)\)', r'assertThat(\1).isNotNull()', content)

        # Replace assertThat(a, is(<null>)) -> assertThat(a).isNull()
        content = re.sub(r'assertThat\(([^,]+),\s*is\(<null>\)\)', r'assertThat(\1).isNull()', content)
        content = re.sub(r'assertThat\(([^,]+),\s*is\(null\)\)', r'assertThat(\1).isNull()', content)

        # Replace assertThat(message, a, is(b)) -> assertThat(a).as(message).isEqualTo(b)
        content = re.sub(r'assertThat\("([^"]+)",\s*([^,]+),\s*is\(([^)]+)\)\)', r'assertThat(\2).as("\1").isEqualTo(\3)', content)

        # Replace assertThat(message, a, notNullValue()) -> assertThat(a).as(message).isNotNull()
        content = re.sub(r'assertThat\("([^"]+)",\s*([^,]+),\s*notNullValue\(\)\)', r'assertThat(\2).as("\1").isNotNull()', content)

        # Replace two-argument assertThat -> assertThat().isEqualTo()
        # Pattern: assertThat(something, something_else)
        # But not: assertThat(something).something
        # This is tricky, so we use a more careful pattern
        def replace_two_arg_assert(match):
            arg1 = match.group(1)
            arg2 = match.group(2)
            # Don't replace if arg2 looks like a matcher call
            if '(' in arg2:
                return match.group(0)  # Keep original
            return f'assertThat({arg1}).isEqualTo({arg2})'

        # Match assertThat with two arguments, where neither contains unbalanced parentheses
        content = re.sub(r'assertThat\(([^,)]+),\s*([^)]+)\)(?!\.))',  replace_two_arg_assert, content)

        # Ensure AssertJ import is present
        if 'import static org.assertj.core.api.Assertions.assertThat;' not in content:
            # Find package declaration and add import after it
            content = re.sub(
                r'(package [^;]+;)',
                r'\1\n\nimport static org.assertj.core.api.Assertions.assertThat;',
                content,
                count=1
            )

        if content != original:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  ✓ Modified: {filepath}")
            return True
        else:
            print(f"  - No changes: {filepath}")
            return False

    except Exception as e:
        print(f"  ✗ Error processing {filepath}: {e}")
        return False

def main():
    """Main entry point"""
    root_dir = '/home/r-uu/develop/github/main/root'

    if len(sys.argv) > 1:
        root_dir = sys.argv[1]

    print(f"Converting Hamcrest to AssertJ in: {root_dir}")
    print("=" * 60)

    modified_count = 0
    total_count = 0

    for root, dirs, files in os.walk(root_dir):
        for file in files:
            if file.endswith('Test.java') or file.endswith('IntegrationTest.java'):
                filepath = os.path.join(root, file)
                total_count += 1
                if convert_file(filepath):
                    modified_count += 1

    print("=" * 60)
    print(f"Conversion complete!")
    print(f"Modified {modified_count} of {total_count} test files")

if __name__ == '__main__':
    main()

