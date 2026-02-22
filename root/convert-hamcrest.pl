#!/usr/bin/perl

use strict;
use warnings;
use File::Find;
use File::Slurp;

my $test_dir = '/home/r-uu/develop/github/main/root/app/jeeeraaah/backend/common/mapping.jpa.dto/src/test/java';

print "Converting Hamcrest to AssertJ in: $test_dir\n";
print "=" x 60 . "\n";

my $modified_count = 0;
my $total_count = 0;

find(sub {
    return unless -f && /\.java$/;

    my $filepath = $File::Find::name;
    print "Processing: $filepath\n";
    $total_count++;

    my $content = read_file($filepath);
    my $original = $content;

    # Replace .isEqualTo(true) with .isTrue()
    $content =~ s/\.isEqualTo\(true\)/.isTrue()/g;

    # Replace .isEqualTo(false) with .isFalse()
    $content =~ s/\.isEqualTo\(false\)/.isFalse()/g;

    # Replace assertThat(a, is(b)) -> assertThat(a).isEqualTo(b)
    $content =~ s/assertThat\(([^,]+),\s*is\(([^)]+)\)\)/assertThat($1).isEqualTo($2)/g;

    # Replace assertThat(a, notNullValue()) -> assertThat(a).isNotNull()
    $content =~ s/assertThat\(([^,]+),\s*notNullValue\(\)\)/assertThat($1).isNotNull()/g;

    # Replace assertThat(a, is(<null>)) -> assertThat(a).isNull()
    $content =~ s/assertThat\(([^,]+),\s*is\(<null>\)\)/assertThat($1).isNull()/g;
    $content =~ s/assertThat\(([^,]+),\s*is\(null\)\)/assertThat($1).isNull()/g;

    # Replace assertThat("message", a, is(b)) -> assertThat(a).as("message").isEqualTo(b)
    $content =~ s/assertThat\("([^"]+)",\s*([^,]+),\s*is\(([^)]+)\)\)/assertThat($2).as("$1").isEqualTo($3)/g;

    # Replace assertThat("message", a, notNullValue()) -> assertThat(a).as("message").isNotNull()
    $content =~ s/assertThat\("([^"]+)",\s*([^,]+),\s*notNullValue\(\)\)/assertThat($2).as("$1").isNotNull()/g;

    # Replace simple two-argument assertThat -> assertThat().isEqualTo()
    # But be careful not to match method calls that already use fluent API
    # This regex looks for assertThat(something, something_else) that is NOT followed by a dot
    $content =~ s/assertThat\(([^,\(]+),\s*([^,\)]+)\)(?!\s*\.)/assertThat($1).isEqualTo($2)/g;

    # Remove Hamcrest imports
    $content =~ s/^import static org\.hamcrest\..*?\n//gm;
    $content =~ s/^import org\.hamcrest\..*?\n//gm;

    # Write back if modified
    if ($content ne $original) {
        write_file($filepath, $content);
        print "  ✓ Modified\n";
        $modified_count++;
    } else {
        print "  - No changes\n";
    }

}, $test_dir);

print "=" x 60 . "\n";
print "Conversion complete!\n";
print "Modified $modified_count of $total_count test files\n";

