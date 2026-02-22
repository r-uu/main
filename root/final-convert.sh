#!/bin/bash

# Robust Hamcrest to AssertJ conversion script

TEST_DIR="/home/r-uu/develop/github/main/root/app/jeeeraaah/backend/common/mapping.jpa.dto/src/test/java"

echo "Converting Hamcrest to AssertJ..."

cd "$TEST_DIR" || exit 1

# Find all Java test files and process them
find . -name "*.java" -type f | while read -r file; do
    echo "Processing: $file"

    # Backup file
    cp "$file" "$file.bak"

    # Use Perl for more powerful regex replacements
    perl -i -pe '
        # Fix assertThat(something, something_else) -> assertThat(something).isEqualTo(something_else)
        # But only if not followed by ) - to avoid nested calls
        s/assertThat\(\s*([^,\(]+),\s*([^,\)]+)\s*\)(?!\s*\))/assertThat($1).isEqualTo($2)/g;

        # Remove remaining Hamcrest imports if any
        s/^import static org\.hamcrest\..*\n//g;
        s/^import org\.hamcrest\..*\n//g;
    ' "$file"

done

echo "Done!"

