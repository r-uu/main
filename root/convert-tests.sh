#!/bin/bash

# Convert Hamcrest assertions to AssertJ in all test files

echo "Converting Hamcrest to AssertJ..."

# Find all test files in the backend mapping module
TEST_DIR="/home/r-uu/develop/github/main/root/app/jeeeraaah/backend/common/mapping.jpa.dto/src/test/java"

if [ ! -d "$TEST_DIR" ]; then
    echo "Error: Test directory not found: $TEST_DIR"
    exit 1
fi

cd "$TEST_DIR" || exit 1

# Process each Java test file
find . -name "*.java" -type f | while IFS= read -r file; do
    echo "Processing: $file"

    # Create backup
    cp "$file" "$file.bak"

    # Use sed to make replacements
    # 1. Replace .isEqualTo(true) with .isTrue()
    sed -i 's/\.isEqualTo(true)/.isTrue()/g' "$file"

    # 2. Replace .isEqualTo(false) with .isFalse()
    sed -i 's/\.isEqualTo(false)/.isFalse()/g' "$file"

    # 3. Replace assertThat(a, is(b)) with assertThat(a).isEqualTo(b)
    # This is complex, so we use perl for better regex
    perl -i -pe 's/assertThat\(([^,]+),\s*is\(([^)]+)\)\)/assertThat($1).isEqualTo($2)/g' "$file"

    # 4. Replace assertThat(a, notNullValue()) with assertThat(a).isNotNull()
    perl -i -pe 's/assertThat\(([^,]+),\s*notNullValue\(\)\)/assertThat($1).isNotNull()/g' "$file"

    # 5. Replace assertThat(a, is(<null>)) with assertThat(a).isNull()
    perl -i -pe 's/assertThat\(([^,]+),\s*is\(<null>\)\)/assertThat($1).isNull()/g' "$file"

    # 6. Replace assertThat("message", a, is(b)) with assertThat(a).as("message").isEqualTo(b)
    perl -i -pe 's/assertThat\("([^"]+)",\s*([^,]+),\s*is\(([^)]+)\)\)/assertThat($2).as("$1").isEqualTo($3)/g' "$file"

    # 7. Replace assertThat("message", a, notNullValue()) with assertThat(a).as("message").isNotNull()
    perl -i -pe 's/assertThat\("([^"]+)",\s*([^,]+),\s*notNullValue\(\)\)/assertThat($2).as("$1").isNotNull()/g' "$file"

    # 8. Remove Hamcrest imports
    sed -i '/import static org\.hamcrest\./d' "$file"
    sed -i '/import org\.hamcrest\./d' "$file"

done

echo "Conversion complete!"

